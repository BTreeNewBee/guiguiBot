package com.iguigui.common.kotlin

import com.google.devtools.ksp.getFunctionDeclarationsByName
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.*
import com.iguigui.common.interfaces.DTO
import java.io.OutputStream


class TestProcessor(
    val codeGenerator: CodeGenerator,
    val options: Map<String, String>,
    val logger : KSPLogger
) : SymbolProcessor {

    lateinit var file: OutputStream

    lateinit var dtoKSType: KSType

    val resultMap = HashMap<KSType,ArrayList<KSFunction>>()

    fun emit(s: String, indent: String) {
        file.appendText("$indent$s\n")
    }


    operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {

        logger.info("process KSAnnotated")
        val symbols = resolver
            // Getting all symbols that are annotated with @Function.
            .getSymbolsWithAnnotation("com.iguigui.common.annotations.SubscribeBotMessage")
            // Making sure we take only class declarations.
            .filterIsInstance<KSFunctionDeclaration>()
        if (!symbols.iterator().hasNext()) return emptyList()
        // The generated file will be located at:
        // build/generated/ksp/main/kotlin/com/morfly/GeneratedFunctions.kt
        val file = codeGenerator.createNewFile(
            // Make sure to associate the generated file with sources to keep/maintain it across incremental builds.
            // Learn more about incremental processing in KSP from the official docs:
            // https://kotlinlang.org/docs/ksp-incremental.html
            dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
            packageName = "com.iguigui.common.kotlin",
            fileName = "GeneratedFunctions"
        )
        // Generating package statement.
        file.appendText("package com.iguigui.common.kotlin\n")
        val classDeclarationByName = resolver.getClassDeclarationByName("com.iguigui.common.interfaces.DTO")
        val dtoKSType = classDeclarationByName?.asStarProjectedType()
        if (dtoKSType == null) {
            return ArrayList()
        }
        this.dtoKSType = dtoKSType
        // Processing each class declaration, annotated with @Function.
        symbols.forEach {
            logger.info("${it.functionKind}")
            it.accept(Visitor(file), Unit)
        }

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

            file += function.functionKind.name
            file += function.parentDeclaration?.qualifiedName?.getQualifier()?:" no parentDeclaration"
            file += function.parameters[0].type.toString()

            if (!dtoKSType.isAssignableFrom(function.parameters[0].type.resolve())) {
                throw RuntimeException("This function be annotated with @SubscribeBotMessage must only have one parameter and sub with com.iguigui.common.interfaces.DTO !")
            }
            val parentDeclaration = function.parentDeclaration
            if (parentDeclaration !is KSClassDeclaration) {
                throw RuntimeException("This function must be a member function !")
            }

            //[ksp] java.lang.RuntimeException: CLASS + com.google.devtools.ksp.symbol.impl.kotlin.KSNameImpl@118a882f + com.google.devtools.ksp.symbol.impl.kotlin.KSNameImpl@1b287888
            throw RuntimeException("${parentDeclaration.classKind} + ${parentDeclaration?.qualifiedName} + ${parentDeclaration?.packageName}")



//            file += function.functionKind.name
//            file += function.parentDeclaration?.qualifiedName?.getQualifier()?:" no parentDeclaration"

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
