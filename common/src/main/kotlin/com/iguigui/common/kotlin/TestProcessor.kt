package com.iguigui.common.kotlin

import com.google.devtools.ksp.getFunctionDeclarationsByName
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.*
import com.iguigui.common.interfaces.DTO
import java.io.OutputStream
import kotlin.reflect.KClass


class TestProcessor(
    val codeGenerator: CodeGenerator,
    val options: Map<String, String>,
    val logger: KSPLogger
) : SymbolProcessor {

    lateinit var file: OutputStream

    lateinit var dtoKSType: KSType

    lateinit var resolver: Resolver

    val resultMap = HashMap<KSType, HashMap<KSClassDeclaration, ArrayList<KSFunctionDeclaration>>>()


    fun emit(s: String, indent: String) {
        file.appendText("$indent$s\n")
    }

    fun OutputStream.appendText(str: String) {
        this.write(str.toByteArray())
    }

    operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        this.resolver = resolver
        //获取所有SubscribeBotMessage注解的function
        val symbols = resolver
            .getSymbolsWithAnnotation("com.iguigui.common.annotations.SubscribeBotMessage")
            .filterIsInstance<KSFunctionDeclaration>()
        if (!symbols.iterator().hasNext()) return emptyList()
        val file = codeGenerator.createNewFile(
            // Make sure to associate the generated file with sources to keep/maintain it across incremental builds.
            // Learn more about incremental processing in KSP from the official docs:
            // https://kotlinlang.org/docs/ksp-incremental.html
            dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
            packageName = "com.iguigui.process.kotlin",
            fileName = "MessageDispatcher"
        )
        // Generating package statement.
        file.appendText("package com.iguigui.process.kotlin\n")
        val classDeclarationByName = resolver.getClassDeclarationByName("com.iguigui.common.interfaces.DTO")
        val dtoKSType = classDeclarationByName?.asStarProjectedType() ?: return ArrayList()
        this.dtoKSType = dtoKSType
        // Processing each class declaration, annotated with @Function.
        symbols.forEach {
            logger.info("${it.functionKind}")
            it.accept(Visitor(file), Unit)
        }

        file.appendText("\n")
        file.appendText("import com.iguigui.process.qqbot.IMessageDispatcher\n")
        file.appendText("import com.iguigui.common.interfaces.DTO\n")
        file.appendText("import org.springframework.beans.factory.annotation.Autowired\n")
        file.appendText("import org.springframework.stereotype.Component\n")

        val classSet = resultMap.values.map { e -> HashSet(e.keys) }.reduce { ac, its ->
            ac.addAll(its)
            return@reduce ac
        }
        classSet.forEach { file.appendText("import ${it.packageName.asString()}.${it.simpleName.getShortName()}\n") }
        resultMap.entries.forEach{
            file.appendText("import com.iguigui.process.qqbot.dto.${it.key.declaration.simpleName.asString()}\n")
        }

        file.appendText("\n")
//        file.appendText("@Component\n")
        file.appendText("class MessageDispatcher : IMessageDispatcher {\n")
        file.appendText("\n")

        classSet.forEach {
            file.appendText("    @Autowired\n")
            file.appendText("    lateinit var ${it.simpleName.getShortName().firstToLowerCase()}: ${it.simpleName.getShortName()}\n")
            file.appendText("\n")
        }

        file.appendText("    override fun handler(message: DTO) {\n")
        file.appendText("        when (message) {\n")
        resultMap.entries.forEach {
            file.appendText("            is ${it.key.declaration.simpleName.asString()} -> {\n")
            it.value.forEach {
                it.value.forEach { function ->
                    file.appendText("                ${it.key.simpleName.asString().firstToLowerCase()}.${function.simpleName.getShortName()}(message)\n")
                }
            }
            file.appendText("            }\n")
        }
        file.appendText("        }\n")
        file.appendText("    }\n")
        file.appendText("}")


        // Don't forget to close the out stream.
        val unableToProcess = symbols.filterNot { it.validate() }.toList()
        return unableToProcess
    }

    inner class Visitor(private val file: OutputStream) : KSVisitorVoid() {

        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {

            if (function.functionKind != FunctionKind.MEMBER) {
                throw RuntimeException("Only member function can be annotated with @SubscribeBotMessage")
                return
            }
            if (function.parameters.size != 1) {
                throw RuntimeException("This function be annotated with @SubscribeBotMessage must only have one parameter !")
            }

//            file += function.functionKind.name
//            file += function.parentDeclaration?.qualifiedName?.getQualifier() ?: " no parentDeclaration"
//            file += function.parameters[0].type.toString()
            val parameterKstype = function.parameters[0].type.resolve()
            if (!dtoKSType.isAssignableFrom(parameterKstype)) {
                throw RuntimeException("This function be annotated with @SubscribeBotMessage must only have one parameter and sub with com.iguigui.common.interfaces.DTO !")
            }
            val pclass = function.parentDeclaration
            if (pclass !is KSClassDeclaration) {
                throw RuntimeException("This function must be a member function !")
            }

            val subscribeBotMessageAnnotation =
                resolver.getClassDeclarationByName("com.iguigui.common.annotations.SubscribeBotMessage")

            if (subscribeBotMessageAnnotation == null) {
                throw RuntimeException("Annotation com.iguigui.common.annotations.SubscribeBotMessage load failed!!")
            }


            resultMap.computeIfAbsent(parameterKstype) { key -> HashMap() }
                .computeIfAbsent(pclass) { key -> ArrayList() }
                .add(function)

        }


        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            if (classDeclaration.classKind != ClassKind.INTERFACE) {
                logger.error("Only interface can be annotated with @Function", classDeclaration)
                return
            }

            // Getting the @Function annotation object.
            val annotation: KSAnnotation = classDeclaration.annotations.first {
                it.shortName.asString() == "Function"
            }

            // Getting the 'name' argument object from the @Function.
            val nameArgument: KSValueArgument = annotation.arguments
                .first { arg -> arg.name?.asString() == "name" }

            // Getting the value of the 'name' argument.
            val functionName = nameArgument.value as String

            // Getting the list of member properties of the annotated interface.
            val properties: Sequence<KSPropertyDeclaration> = classDeclaration.getAllProperties()
                .filter { it.validate() }

            // Generating function signature.
            file += "\n"
            if (properties.iterator().hasNext()) {
                file += "fun $functionName(\n"

                // Iterating through each property to translate them to function arguments.
                properties.forEach { prop ->
                    visitPropertyDeclaration(prop, Unit)
                }
                file += ") {\n"

            } else {
                // Otherwise, generating function with no args.
                file += "fun $functionName() {\n"
            }

            // Generating function body.
            file += "    println(\"Hello from $functionName\")\n"
            file += "}\n"
        }

        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
            // Generating argument name.
            val argumentName = property.simpleName.asString()
            file += "    $argumentName: "

            // Generating argument type.
            val resolvedType: KSType = property.type.resolve()
            file += resolvedType.declaration.qualifiedName?.asString() ?: run {
                logger.error("Invalid property type", property)
                return
            }

            // Generating generic parameters if any.
            val genericArguments: List<KSTypeArgument> = property.type.element?.typeArguments ?: emptyList()
            visitTypeArguments(genericArguments)

            // Handling nullability.
            file += if (resolvedType.nullability == Nullability.NULLABLE) "?" else ""

            file += ",\n"
        }

        private fun visitTypeArguments(typeArguments: List<KSTypeArgument>) {
            if (typeArguments.isNotEmpty()) {
                file += "<"
                typeArguments.forEachIndexed { i, arg ->
                    visitTypeArgument(arg, data = Unit)
                    if (i < typeArguments.lastIndex) file += ", "
                }
                file += ">"
            }
        }

        override fun visitTypeArgument(typeArgument: KSTypeArgument, data: Unit) {
            // Handling KSP options, specified in the consumer's build.gradle(.kts) file.
            if (options["ignoreGenericArgs"] == "true") {
                file += "*"
                return
            }

            when (val variance: Variance = typeArgument.variance) {
                // <*>
                Variance.STAR -> {
                    file += "*"
                    return
                }
                // <out ...>, <in ...>
                Variance.COVARIANT, Variance.CONTRAVARIANT -> {
                    file += variance.label
                    file += " "
                }
                Variance.INVARIANT -> {
                    // Do nothing.
                }
            }
            val resolvedType: KSType? = typeArgument.type?.resolve()
            file += resolvedType?.declaration?.qualifiedName?.asString() ?: run {
                logger.error("Invalid type argument", typeArgument)
                return
            }

            // Generating nested generic parameters if any.
            val genericArguments: List<KSTypeArgument> = typeArgument.type?.element?.typeArguments ?: emptyList()
            visitTypeArguments(genericArguments)

            // Handling nullability.
            file += if (resolvedType?.nullability == Nullability.NULLABLE) "?" else ""
        }
    }


}


fun String.firstToLowerCase():String {
    val toCharArray = this.toCharArray()
    if (toCharArray.isEmpty()) {
        return this
    }
    if (toCharArray[0] in 'A' ..'Z') {
        toCharArray[0] = toCharArray[0] + 0x20
    }
    return String(toCharArray)
}
