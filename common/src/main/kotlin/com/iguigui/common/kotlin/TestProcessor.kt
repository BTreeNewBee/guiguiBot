package com.iguigui.common.kotlin

import com.google.devtools.ksp.getFunctionDeclarationsByName
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.*
import java.io.OutputStream


class TestProcessor(
    val codeGenerator: CodeGenerator,
    val options: Map<String, String>,
    val logger : KSPLogger
) : SymbolProcessor {
    lateinit var file: OutputStream
    var invoked = false

    fun emit(s: String, indent: String) {
        file.appendText("$indent$s\n")
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) {
            return emptyList()
        }
        val symbols = resolver
            // Getting all symbols that are annotated with @Function.
            .getSymbolsWithAnnotation("com.morfly.Function")
            // Making sure we take only class declarations.
            .filterIsInstance<KSClassDeclaration>()
        // The generated file will be located at:
        // build/generated/ksp/main/kotlin/com/morfly/GeneratedFunctions.kt
        val file = codeGenerator.createNewFile(
            // Make sure to associate the generated file with sources to keep/maintain it across incremental builds.
            // Learn more about incremental processing in KSP from the official docs:
            // https://kotlinlang.org/docs/ksp-incremental.html
            dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
            packageName = "com.morfly",
            fileName = "GeneratedFunctions"
        )
        // Generating package statement.
        file.appendText("package com.morfly\n")

        // Processing each class declaration, annotated with @Function.
        symbols.forEach {
            logger.info("${it.classKind}")
            it.accept(Visitor(file), Unit)
        }

        // Don't forget to close the out stream.
        file.close()

        val unableToProcess = symbols.filterNot { it.validate() }.toList()
        return unableToProcess
    }

    inner class Visitor(private val file: OutputStream) : KSVisitorVoid() {

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

    inner class TestVisitor : KSVisitor<String, Unit> {

        override fun visitReferenceElement(element: KSReferenceElement, data: String) {
        }

        override fun visitModifierListOwner(modifierListOwner: KSModifierListOwner, data: String) {
            TODO("Not yet implemented")
        }

        override fun visitNode(node: KSNode, data: String) {
            TODO("Not yet implemented")
        }

        override fun visitPropertyAccessor(accessor: KSPropertyAccessor, data: String) {
            TODO("Not yet implemented")
        }

        override fun visitDynamicReference(reference: KSDynamicReference, data: String) {
            TODO("Not yet implemented")
        }
        val visited = HashSet<Any>()

        private fun checkVisited(symbol: Any): Boolean {
            return if (visited.contains(symbol)) {
                true
            } else {
                visited.add(symbol)
                false
            }
        }

        private fun invokeCommonDeclarationApis(declaration: KSDeclaration, indent: String) {
            emit(
              "${declaration.modifiers.joinToString(" ")} ${declaration.simpleName.asString()}", indent
            )
            declaration.annotations.forEach{ it.accept(this, "$indent  ") }
            if (declaration.parentDeclaration != null)
                emit("  enclosing: ${declaration.parentDeclaration!!.qualifiedName?.asString()}", indent)
            declaration.containingFile?.let { emit("${it.packageName.asString()}.${it.fileName}", indent) }
            declaration.typeParameters.forEach { it.accept(this, "$indent  ") }
        }

        override fun visitFile(file: KSFile, data: String) {
            if (checkVisited(file)) return
            file.annotations.forEach{ it.accept(this, "$data  ") }
            emit(file.packageName.asString(), data)
            for (declaration in file.declarations) {
                declaration.accept(this, data)
            }
        }

        override fun visitAnnotation(annotation: KSAnnotation, data: String) {
            if (checkVisited(annotation)) return
            emit("annotation", data)
            annotation.annotationType.accept(this, "$data  ")
            annotation.arguments.forEach { it.accept(this, "$data  ") }
        }

        override fun visitCallableReference(reference: KSCallableReference, data: String) {
            if (checkVisited(reference)) return
            emit("element: ", data)
            reference.functionParameters.forEach { it.accept(this, "$data  ") }
            reference.receiverType?.accept(this, "$data receiver")
            reference.returnType.accept(this, "$data  ")
        }

        override fun visitPropertyGetter(getter: KSPropertyGetter, data: String) {
            if (checkVisited(getter)) return
            emit("propertyGetter: ", data)
            getter.annotations.forEach { it.accept(this, "$data  ") }
            emit(getter.modifiers.joinToString(" "), data)
            getter.returnType?.accept(this, "$data  ")
        }

        override fun visitPropertySetter(setter: KSPropertySetter, data: String) {
            if (checkVisited(setter)) return
            emit("propertySetter: ", data)
            setter.annotations.forEach { it.accept(this, "$data  ") }
            emit(setter.modifiers.joinToString(" "), data)
        }

        override fun visitTypeArgument(typeArgument: KSTypeArgument, data: String) {
            if (checkVisited(typeArgument)) return
            typeArgument.annotations.forEach{ it.accept(this, "$data  ") }
            emit(
              when (typeArgument.variance) {
                  Variance.STAR -> "*"
                  Variance.COVARIANT -> "out"
                  Variance.CONTRAVARIANT -> "in"
                  else -> ""
              }, data
            )
            typeArgument.type?.accept(this, "$data  ")
        }

        override fun visitTypeParameter(typeParameter: KSTypeParameter, data: String) {
            if (checkVisited(typeParameter)) return
            typeParameter.annotations.forEach{ it.accept(this, "$data  ") }
            if (typeParameter.isReified) {
                emit("reified ", data)
            }
            emit(
              when (typeParameter.variance) {
                  Variance.COVARIANT -> "out "
                  Variance.CONTRAVARIANT -> "in "
                  else -> ""
              } + typeParameter.name.asString(), data
            )
            if (typeParameter.bounds.toList().isNotEmpty()) {
                typeParameter.bounds.forEach { it.accept(this, "$data  ") }
            }
        }

        override fun visitValueParameter(valueParameter: KSValueParameter, data: String) {
            if (checkVisited(valueParameter)) return
            valueParameter.annotations.forEach { it.accept(this, "$data  ") }
            if (valueParameter.isVararg) {
                emit("vararg", "$data  ")
            }
            if (valueParameter.isNoInline) {
                emit("noinline", "$data  ")
            }
            if (valueParameter.isCrossInline) {
                emit("crossinline ", "$data  ")
            }
            emit(valueParameter.name?.asString() ?: "_", "$data  ")
            valueParameter.type.accept(this, "$data  ")
        }

        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: String) {
            if (checkVisited(function)) return
            invokeCommonDeclarationApis(function, data)
            for (declaration in function.declarations) {
                declaration.accept(this, "$data  ")
            }
            function.parameters.forEach { it.accept(this, "$data  ") }
            function.typeParameters.forEach { it.accept(this, "$data  ") }
            function.extensionReceiver?.accept(this, "$data extension:")
            emit("returnType:", data)
            function.returnType?.accept(this, "$data  ")
        }

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: String) {
            if (checkVisited(classDeclaration)) return
            invokeCommonDeclarationApis(classDeclaration, data)
            emit(classDeclaration.classKind.type, data)
            for (declaration in classDeclaration.declarations) {
                declaration.accept(this, "$data ")
            }
            classDeclaration.superTypes.forEach { it.accept(this, "$data  ") }
            classDeclaration.primaryConstructor?.accept(this, "$data  ")
        }

        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: String) {
            if (checkVisited(property)) return
            invokeCommonDeclarationApis(property, data)
            property.type.accept(this, "$data  ")
            property.extensionReceiver?.accept(this, "$data extension:")
            property.setter?.accept(this, "$data  ")
            property.getter?.accept(this, "$data  ")
        }

        override fun visitTypeReference(typeReference: KSTypeReference, data: String) {
            if (checkVisited(typeReference)) return
            typeReference.annotations.forEach{ it.accept(this, "$data  ") }
            val type = typeReference.resolve()
            type.let {
                emit("resolved to: ${it.declaration.qualifiedName?.asString()}", data)
            }
            try {
                typeReference.element?.accept(this, "$data  ")
            } catch (e: IllegalStateException) {
                emit("TestProcessor: exception: $e", data)
            }
        }

        override fun visitAnnotated(annotated: KSAnnotated, data: String) {
        }

        override fun visitDeclaration(declaration: KSDeclaration, data: String) {
        }

        override fun visitDeclarationContainer(declarationContainer: KSDeclarationContainer, data: String) {
        }

        override fun visitParenthesizedReference(reference: KSParenthesizedReference, data: String) {
        }

        override fun visitClassifierReference(reference: KSClassifierReference, data: String) {
            if (checkVisited(reference)) return
            if (reference.typeArguments.isNotEmpty()) {
                reference.typeArguments.forEach { it.accept(this, "$data  ") }
            }
        }

        override fun visitTypeAlias(typeAlias: KSTypeAlias, data: String) {
        }

        override fun visitValueArgument(valueArgument: KSValueArgument, data: String) {
            if (checkVisited(valueArgument)) return
            val name = valueArgument.name?.asString() ?: "<no name>"
            emit("$name: ${valueArgument.value}", data)
            valueArgument.annotations.forEach { it.accept(this, "$data  ") }
        }
    }

}

class TestProcessorProvider : SymbolProcessorProvider {
    override fun create(
        env: SymbolProcessorEnvironment
    ): SymbolProcessor {
        return TestProcessor(env.codeGenerator, env.options,env.logger)
    }
}

operator fun OutputStream.plusAssign(str: String) {
    this.write(str.toByteArray())
}