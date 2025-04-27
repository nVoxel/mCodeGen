package com.voxeldev.mcodegen.dsl.language.java

import com.voxeldev.mcodegen.dsl.ir.IrAssignmentExpression.IrAssignmentOperator
import com.voxeldev.mcodegen.dsl.ir.IrBinaryExpression.IrBinaryOperator
import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrClassKind
import com.voxeldev.mcodegen.dsl.ir.IrExpression
import com.voxeldev.mcodegen.dsl.ir.IrFile
import com.voxeldev.mcodegen.dsl.ir.IrStatement
import com.voxeldev.mcodegen.dsl.ir.IrStringRepresentation
import com.voxeldev.mcodegen.dsl.ir.IrType
import com.voxeldev.mcodegen.dsl.ir.IrUnaryExpression.IrUnaryOperator
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPrivate
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityProtected
import com.voxeldev.mcodegen.dsl.ir.IrVisibilityPublic
import com.voxeldev.mcodegen.dsl.ir.builders.IrClassBuilder
import com.voxeldev.mcodegen.dsl.ir.builders.irAnnotation
import com.voxeldev.mcodegen.dsl.ir.builders.irArrayType
import com.voxeldev.mcodegen.dsl.ir.builders.irAssignmentExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irBinaryExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irBlockStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irBreakStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irCastExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irClass
import com.voxeldev.mcodegen.dsl.ir.builders.irContinueStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irDoWhileStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irExpressionStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irField
import com.voxeldev.mcodegen.dsl.ir.builders.irFile
import com.voxeldev.mcodegen.dsl.ir.builders.irForStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irIdentifierExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irIfStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irImport
import com.voxeldev.mcodegen.dsl.ir.builders.irLiteralExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irMethod
import com.voxeldev.mcodegen.dsl.ir.builders.irMethodBody
import com.voxeldev.mcodegen.dsl.ir.builders.irMethodCallExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irObjectCreationExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irParameter
import com.voxeldev.mcodegen.dsl.ir.builders.irReturnStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irSwitchStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irSwitchStatementCase
import com.voxeldev.mcodegen.dsl.ir.builders.irTernaryExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irThrowStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irTryCatchStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irTryCatchStatementClause
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeCheckExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeParameter
import com.voxeldev.mcodegen.dsl.ir.builders.irTypePrimitive
import com.voxeldev.mcodegen.dsl.ir.builders.irTypeReference
import com.voxeldev.mcodegen.dsl.ir.builders.irUnaryExpression
import com.voxeldev.mcodegen.dsl.ir.builders.irVariableDeclarationStatement
import com.voxeldev.mcodegen.dsl.ir.builders.irWhileStatement
import com.voxeldev.mcodegen.dsl.language.base.LanguageModule
import com.voxeldev.mcodegen.dsl.language.java.ir.IrVisibilityPackagePrivate
import com.voxeldev.mcodegen.dsl.language.java.ir.builders.irExpressionUnknown
import com.voxeldev.mcodegen.dsl.language.java.ir.builders.irStatementUnknown
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import com.voxeldev.mcodegen.dsl.source.edit.scenario.EditScenario
import com.voxeldev.mcodegen.dsl.source.generate.mapper.GenerationMapper
import com.voxeldev.mcodegen.utils.GlobalFileUtils
import com.voxeldev.mcodegen.utils.GlobalFileUtils.asString
import org.jetbrains.kotlin.com.intellij.psi.PsiArrayType
import org.jetbrains.kotlin.com.intellij.psi.PsiAssignmentExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiBinaryExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiBlockStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiBreakStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiClass
import org.jetbrains.kotlin.com.intellij.psi.PsiClassType
import org.jetbrains.kotlin.com.intellij.psi.PsiConditionalExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiContinueStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiDeclarationStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiDoWhileStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiExpressionStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiForStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiIfStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiInstanceOfExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiLiteralExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiMethodCallExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiModifier
import org.jetbrains.kotlin.com.intellij.psi.PsiNewExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiPostfixExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiPrefixExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiPrimitiveType
import org.jetbrains.kotlin.com.intellij.psi.PsiReferenceExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiReturnStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiSwitchLabelStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiSwitchStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiThrowStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiTryStatement
import org.jetbrains.kotlin.com.intellij.psi.PsiType
import org.jetbrains.kotlin.com.intellij.psi.PsiTypeCastExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiVariable
import org.jetbrains.kotlin.com.intellij.psi.PsiWhileStatement
import java.io.File
import kotlin.io.path.Path

object JavaModule : LanguageModule {

    const val PSI_CLASS = "psi_class"

    private val visitedClasses = hashMapOf<String, IrClassBuilder>()
    private val resolvedTypes = hashMapOf<String, IrType>()

    context(ScenarioScope)
    override fun parse(sourcePath: String): IrFile {
        val fileName = Path(sourcePath).fileName.toString()
        val codeString = File(sourcePath).asString()

        val psiFile = GlobalFileUtils.parseJavaFile(codeString, fileName)
        val irFileBuilder = irFile(fileName)

        visitedClasses.clear()
        resolvedTypes.clear()

        irFileBuilder.addLanguageProperty("package", psiFile.packageName)

        psiFile.importList?.importStatements?.forEach { importStatement ->
            val path = importStatement.qualifiedName ?: return@forEach
            irFileBuilder.addImport(
                import = irImport(
                    path = path,
                    isWildcard = path.endsWith('*')
                ).build()
            )
        }

        psiFile.classes.forEach { psiClass ->
            irFileBuilder.addDeclaration(convertClass(psiClass))
        }

        return irFileBuilder.build()
    }

    private fun convertClass(psiClass: PsiClass): IrClass {
        val className = psiClass.qualifiedName ?: "Ir:UnnamedClass"
        if (visitedClasses.contains(className)) {
            return visitedClasses[className]!!.build()
        }

        val irClassBuilder = irClass(className)
        visitedClasses[className] = irClassBuilder

        irClassBuilder.kind(
            when {
                psiClass.isAnnotationType -> IrClassKind.ANNOTATION
                psiClass.isEnum -> IrClassKind.ENUM
                psiClass.isInterface -> IrClassKind.INTERFACE
                else -> IrClassKind.CLASS
            }
        )

        irClassBuilder.visibility(
            when {
                psiClass.hasModifierProperty(PsiModifier.PUBLIC) -> publicVisibility()

                psiClass.hasModifierProperty(PsiModifier.PROTECTED) -> protectedVisibility()

                psiClass.hasModifierProperty(PsiModifier.PRIVATE) -> privateVisibility()

                else -> if (psiClass.isInterface) publicVisibility() else IrVisibilityPackagePrivate()
            }
        )

        if (psiClass.hasModifierProperty(PsiModifier.ABSTRACT)) {
            irClassBuilder.addLanguageProperty(
                PsiModifier.ABSTRACT, true
            )
        }
        if (psiClass.hasModifierProperty(PsiModifier.FINAL)) {
            irClassBuilder.addLanguageProperty(
                PsiModifier.FINAL, true
            )
        }
        if (psiClass.hasModifierProperty(PsiModifier.STATIC)) {
            irClassBuilder.addLanguageProperty(
                PsiModifier.STATIC, true
            )
        }

        psiClass.typeParameters.forEach { typeParameter ->
            val irTypeParameter = irTypeParameter(typeParameter.name ?: "Ir:UnnamedTypeParameter")
            typeParameter.extendsListTypes.forEach { extendsType ->
                val referencedClass = extendsType.resolve() ?: return@forEach
                val referencedClassName = referencedClass.qualifiedName ?: return@forEach
                irTypeParameter.addExtendsType(
                    extendsType = irTypeReference(referencedClassName).apply {
                        addLanguageProperty(PSI_CLASS, referencedClass)
                    }.build()
                )
            }
            irClassBuilder.addTypeParameter(irTypeParameter.build())
        }

        // Convert superclass and interfaces
        psiClass.extendsList?.referencedTypes?.forEach { type ->
            val resolvedClass = type.resolve() ?: return@forEach
            irClassBuilder.addSuperClass(convertClass(resolvedClass))
        }
        psiClass.implementsList?.referencedTypes?.forEach { type ->
            val resolvedInterface = type.resolve() ?: return@forEach
            irClassBuilder.addSuperClass(convertClass(resolvedInterface))
        }

        // Convert fields
        psiClass.fields.forEach { field ->
            val name = field.name ?: return@forEach

            val irFieldBuilder = irField(
                name = name,
                type = convertType(field.type),
            )

            irFieldBuilder.visibility(
                when {
                    field.hasModifierProperty(PsiModifier.PUBLIC) -> publicVisibility()
                    field.hasModifierProperty(PsiModifier.PROTECTED) -> protectedVisibility()
                    field.hasModifierProperty(PsiModifier.PRIVATE) -> privateVisibility()
                    else -> if (psiClass.isInterface) publicVisibility() else IrVisibilityPackagePrivate()
                }
            )

            if (field.hasModifierProperty(PsiModifier.STATIC)) {
                irFieldBuilder.addLanguageProperty(
                    PsiModifier.STATIC, true
                )
            }

            irFieldBuilder.mutable(isMutable = field.hasModifierProperty(PsiModifier.FINAL))

            if (field.hasInitializer()) {
                irFieldBuilder.initializer(
                    initializer = irExpressionStatement(
                        expression = convertExpression(field.initializer!!)
                    ).build()
                )
            }

            irClassBuilder.addField(irFieldBuilder.build())
        }

        // Convert methods
        psiClass.methods.forEach { method ->
            val irMethodBuilder = irMethod(
                name = method.name,
                returnType = convertType(method.returnType),
            )

            irMethodBuilder.isConstructor(isConstructor = method.isConstructor)

            irMethodBuilder.visibility(
                when {
                    method.hasModifierProperty(PsiModifier.PUBLIC) -> publicVisibility()
                    method.hasModifierProperty(PsiModifier.PROTECTED) -> protectedVisibility()
                    method.hasModifierProperty(PsiModifier.PRIVATE) -> privateVisibility()
                    else -> if (psiClass.isInterface) publicVisibility() else IrVisibilityPackagePrivate()
                }
            )


            irMethodBuilder.isAbstract(method.hasModifierProperty(PsiModifier.ABSTRACT))
            irMethodBuilder.isStatic(method.hasModifierProperty(PsiModifier.STATIC))
            irMethodBuilder.isOverride(method.hasAnnotation(Override::class.qualifiedName!!))

            if (method.hasModifierProperty(PsiModifier.NATIVE)) {
                irMethodBuilder.addLanguageProperty(
                    PsiModifier.NATIVE, true
                )
            }

            method.annotations.forEach { annotation ->
                irMethodBuilder.addAnnotation(
                    irAnnotation(annotation.text).build()
                )
            }

            method.parameterList.parameters.forEach { parameter ->
                irMethodBuilder.addParameter(
                    irParameter(
                        name = parameter.name ?: "",
                        type = convertType(parameter.type),
                    ).build()
                )
            }

            // Convert method body if present
            if (method.body != null) {
                val irMethodBodyBuilder = irMethodBody()
                method.body?.statements?.forEach { statement ->
                    irMethodBodyBuilder.addStatement(
                        statement = convertStatement(psiStatement = statement),
                    )
                }
                irMethodBuilder.body(irMethodBodyBuilder.build())
            }

            irClassBuilder.addMethod(irMethodBuilder.build())
        }

        // Convert nested classes
        psiClass.innerClasses.forEach { innerClass ->
            irClassBuilder.addNestedClass(convertClass(innerClass))
        }

        return irClassBuilder.build()
    }

    private fun convertStatement(psiStatement: PsiStatement): IrStatement {
        return when (psiStatement) {
            is PsiExpressionStatement -> {
                irExpressionStatement(expression = convertExpression(psiStatement.expression)).build()
            }

            is PsiDeclarationStatement -> {
                irBlockStatement().apply {
                    psiStatement.declaredElements
                        .mapNotNull { it as? PsiVariable }
                        .forEach { variable ->
                            addStatement(
                                statement = irVariableDeclarationStatement(
                                    name = variable.name ?: "Ir:UnnamedVariable",
                                    type = variable.type.canonicalText,
                                ).apply {
                                    variable.initializer?.let { variableInitializer ->
                                        initializer(initializer = convertExpression(variableInitializer))
                                    }
                                }.build()
                            )
                        }
                }.build()
            }

            is PsiBlockStatement -> {
                irBlockStatement().apply {
                    psiStatement.codeBlock.statements.forEach { innerStatement ->
                        addStatement(convertStatement(psiStatement = innerStatement))
                    }
                }.build()
            }

            is PsiIfStatement -> {
                irIfStatement(condition = convertExpression(psiStatement.condition)).apply {
                    psiStatement.thenBranch?.let { thenBranch ->
                        thenStatement(statement = convertStatement(thenBranch))
                    }
                    psiStatement.elseBranch?.let { elseBranch ->
                        elseStatement(statement = convertStatement(elseBranch))
                    }
                }.build()
            }

            is PsiForStatement -> {
                irForStatement().apply {
                    psiStatement.initialization?.let { initialization ->
                        initializer(initializer = convertStatement(initialization))
                    }

                    psiStatement.condition?.let { condition ->
                        condition(condition = convertExpression(condition))
                    }

                    psiStatement.update?.let { update ->
                        update(update = convertStatement(update))
                    }

                    psiStatement.body?.let { body ->
                        body(statement = convertStatement(body))
                    }
                }.build()
            }

            is PsiWhileStatement -> {
                irWhileStatement(
                    condition = convertExpression(psiStatement.condition),
                ).apply {
                    psiStatement.body?.let { body ->
                        body(statement = convertStatement(body))
                    }
                }.build()
            }

            is PsiDoWhileStatement -> {
                irDoWhileStatement(
                    condition = convertExpression(psiStatement.condition),
                ).apply {
                    psiStatement.body?.let { body ->
                        body(statement = convertStatement(body))
                    }
                }.build()
            }

            is PsiSwitchStatement -> {
                irSwitchStatement(
                    expression = convertExpression(psiStatement.expression)
                ).apply {
                    psiStatement.body?.statements?.let { bodyStatements ->
                        val cases = bodyStatements.splitWhen { statement ->
                            statement is PsiSwitchLabelStatement
                        }.filter { it.size > 1 }

                        cases.forEach { case ->
                            val switchLabelStatement = case[0] as? PsiSwitchLabelStatement ?: return@forEach
                            val matchExpression =
                                switchLabelStatement.caseValues?.expressions?.getOrNull(0) ?: return@forEach
                            addCase(
                                case = irSwitchStatementCase().apply {
                                    if (!switchLabelStatement.isDefaultCase) {
                                        matchExpression(matchExpression = convertExpression(matchExpression))
                                    }

                                    body(
                                        statement = irBlockStatement().apply {
                                            case.drop(1).forEach { caseBodyStatement ->
                                                addStatement(statement = convertStatement(caseBodyStatement))
                                            }
                                        }.build()
                                    )
                                }.build()
                            )
                        }
                    }
                }.build()
            }

            is PsiReturnStatement -> {
                irReturnStatement().apply {
                    psiStatement.returnValue?.let { returnValue ->
                        expression(expression = convertExpression(returnValue))
                    }
                }.build()
            }

            is PsiBreakStatement -> {
                irBreakStatement().build()
            }

            is PsiContinueStatement -> {
                irContinueStatement().build()
            }

            is PsiThrowStatement -> {
                irThrowStatement(
                    expression = convertExpression(psiStatement.exception)
                ).build()
            }

            is PsiTryStatement -> {
                irTryCatchStatement(
                    tryBlock = irBlockStatement().apply {
                        psiStatement.tryBlock?.statements?.forEach { tryBlockStatement ->
                            addStatement(statement = convertStatement(tryBlockStatement))
                        }
                    }.build()
                ).apply {
                    psiStatement.catchSections.forEach { catchSection ->
                        addCatchClause(
                            clause = irTryCatchStatementClause(
                                exceptionType = catchSection.catchType?.canonicalText ?: "Ir:UnnamedType"
                            ).apply {
                                catchSection.parameter?.name?.let { parameterName ->
                                    exceptionName(parameterName)
                                }

                                catchSection.catchBlock?.statements?.let { statements ->
                                    body(
                                        statement = irBlockStatement().apply {
                                            statements.forEach { catchBlockStatement ->
                                                addStatement(statement = convertStatement(catchBlockStatement))
                                            }
                                        }.build()
                                    )
                                }
                            }.build()
                        )
                    }

                    psiStatement.finallyBlock?.statements?.let { finallyBlockStatements ->
                        finallyBlock(
                            statement = irBlockStatement().apply {
                                finallyBlockStatements.forEach { finallyBlockStatement ->
                                    addStatement(statement = convertStatement(finallyBlockStatement))
                                }
                            }.build()
                        )
                    }
                }.build()
            }

            else -> {
                irStatementUnknown().apply {
                    addStringRepresentation(
                        IrStringRepresentation("java", psiStatement.text)
                    )
                }.build()
            }
        }
    }

    private fun convertExpression(psiExpression: PsiExpression?): IrExpression {
        if (psiExpression == null) {
            return irLiteralExpression("").build()
        }

        return when (psiExpression) {
            is PsiLiteralExpression -> {
                irLiteralExpression(psiExpression.text).build()
            }

            is PsiReferenceExpression -> {
                irIdentifierExpression(psiExpression.text).build()
            }

            is PsiMethodCallExpression -> {
                val methodName = psiExpression.resolveMethod()?.name ?: "Ir:UnnamedMethod"
                irMethodCallExpression(methodName = methodName).apply {
                    val receiverExpression = psiExpression.methodExpression.qualifierExpression
                    receiverExpression?.let {
                        receiver(convertExpression(receiverExpression))
                    }
                    psiExpression.argumentList.expressions.forEach { argumentExpression ->
                        addArgument(convertExpression(argumentExpression))
                    }
                }.build()
            }

            is PsiNewExpression -> {
                val className = psiExpression.classReference?.qualifiedName ?: "Ir:UnnamedClass"
                irObjectCreationExpression(className).apply {
                    psiExpression.argumentList?.expressions?.forEach { argumentExpression ->
                        addConstructorArg(convertExpression(argumentExpression))
                    }
                }.build()
            }

            is PsiBinaryExpression -> {
                irBinaryExpression(
                    left = convertExpression(psiExpression.lOperand),
                    operator = convertBinaryOperator(psiExpression.operationSign.text),
                    right = convertExpression(psiExpression.rOperand),
                ).build()
            }

            is PsiPrefixExpression -> {
                irUnaryExpression(
                    operator = convertUnaryOperator(operator = psiExpression.operationSign.text),
                    operand = convertExpression(psiExpression.operand),
                    isPrefix = true,
                ).build()
            }

            is PsiPostfixExpression -> {
                irUnaryExpression(
                    operator = convertUnaryOperator(operator = psiExpression.operationSign.text),
                    operand = convertExpression(psiExpression.operand),
                    isPrefix = false,
                ).build()
            }

            is PsiAssignmentExpression -> {
                irAssignmentExpression(
                    target = convertExpression(psiExpression.lExpression),
                    operator = convertAssignmentOperator(operator = psiExpression.operationSign.text),
                    value = convertExpression(psiExpression.rExpression)
                ).build()
            }

            is PsiConditionalExpression -> {
                irTernaryExpression(
                    condition = convertExpression(psiExpression.condition),
                    ifTrue = convertExpression(psiExpression.thenExpression),
                    ifFalse = convertExpression(psiExpression.elseExpression),
                ).build()
            }

            is PsiTypeCastExpression -> {
                irCastExpression(
                    expression = convertExpression(psiExpression.operand),
                    targetType = psiExpression.castType?.type?.canonicalText ?: psiExpression.castType?.text
                    ?: "Ir:UnnamedType"
                ).build()
            }

            is PsiInstanceOfExpression -> {
                irTypeCheckExpression(
                    expression = convertExpression(psiExpression.operand),
                    checkType = psiExpression.checkType?.type?.canonicalText ?: psiExpression.checkType?.text
                    ?: "Ir:UnnamedType"
                ).build()
            }

            else -> {
                irExpressionUnknown().apply {
                    addStringRepresentation(
                        IrStringRepresentation("java", psiExpression.text)
                    )
                }.build()
            }
        }
    }

    private fun convertType(psiType: PsiType?): IrType {
        if (psiType == null) {
            return irTypePrimitive("void").build()
        }

        return when (psiType) {
            is PsiPrimitiveType -> {
                irTypePrimitive(psiType.presentableText).apply {
                    nullable(false)
                }.build()
            }

            is PsiClassType -> {
                val resolvedClass = psiType.resolve() ?: return getFallbackType(psiType)
                val resolvedClassName = resolvedClass.qualifiedName ?: return getFallbackType(psiType)

                if (resolvedTypes.contains(resolvedClassName)) {
                    resolvedTypes[resolvedClassName]!!
                } else {
                    irTypeReference(referencedClassName = resolvedClassName).apply {
                        nullable(true)
                        addLanguageProperty(PSI_CLASS, resolvedClass)
                    }.build().also { irTypeReference ->
                        resolvedTypes[resolvedClassName] = irTypeReference
                    }
                }
            }

            is PsiArrayType -> {
                irArrayType(convertType(psiType.componentType))
                    .build()
            }

            else -> {
                getFallbackType(psiType)
            }
        }
    }

    context(ScenarioScope)
    override fun generate(
        source: IrFile,
        applyToBasePath: String,
        mappers: List<GenerationMapper>,
    ) {
        TODO()
    }

    context(ScenarioScope)
    override fun edit(sourcePath: String, editScenario: EditScenario) {
        TODO()
    }

    private fun publicVisibility(): IrVisibilityPublic = IrVisibilityPublic(
        IrStringRepresentation(
            "java",
            "public"
        )
    )

    private fun protectedVisibility(): IrVisibilityProtected = IrVisibilityProtected(
        IrStringRepresentation(
            "java",
            "protected"
        )
    )

    private fun privateVisibility(): IrVisibilityPrivate = IrVisibilityPrivate(
        IrStringRepresentation(
            "java",
            "private"
        )
    )

    private fun convertBinaryOperator(operator: String): IrBinaryOperator = when (operator) {
        "+" -> IrBinaryOperator.Plus()
        "-" -> IrBinaryOperator.Minus()
        "*" -> IrBinaryOperator.Multiply()
        "/" -> IrBinaryOperator.Divide()
        "%" -> IrBinaryOperator.Modulo()
        "==" -> IrBinaryOperator.Equals()
        "!=" -> IrBinaryOperator.NotEquals()
        ">" -> IrBinaryOperator.Greater()
        ">=" -> IrBinaryOperator.GreaterOrEqual()
        "<" -> IrBinaryOperator.Less()
        "<=" -> IrBinaryOperator.LessOrEqual()
        "&&" -> IrBinaryOperator.And()
        "||" -> IrBinaryOperator.Or()
        "&" -> IrBinaryOperator.BitwiseAnd()
        "|" -> IrBinaryOperator.BitwiseOr()
        "^" -> IrBinaryOperator.BitwiseXor()
        "<<" -> IrBinaryOperator.ShiftLeft()
        ">>" -> IrBinaryOperator.ShiftRight()
        else -> IrBinaryOperatorUnknown(operator = operator)
    }

    private fun convertUnaryOperator(operator: String): IrUnaryOperator = when (operator) {
        "!" -> IrUnaryOperator.Not()
        "+" -> IrUnaryOperator.Plus()
        "-" -> IrUnaryOperator.Minus()
        "++" -> IrUnaryOperator.Increment()
        "--" -> IrUnaryOperator.Decrement()
        else -> IrUnaryOperatorUnknown(operator = operator)
    }

    private fun convertAssignmentOperator(operator: String): IrAssignmentOperator = when (operator) {
        "=" -> IrAssignmentOperator.Assign()
        "+=" -> IrAssignmentOperator.PlusAssign()
        "-=" -> IrAssignmentOperator.MinusAssign()
        "*=" -> IrAssignmentOperator.MultiplyAssign()
        "/=" -> IrAssignmentOperator.DivideAssign()
        "%=" -> IrAssignmentOperator.ModuloAssign()
        else -> IrAssignmentOperatorUnknown(operator = operator)
    }

    data class IrBinaryOperatorUnknown(
        val operator: String,
    ) : IrBinaryOperator

    data class IrUnaryOperatorUnknown(
        val operator: String,
    ) : IrUnaryOperator

    data class IrAssignmentOperatorUnknown(
        val operator: String,
    ) : IrAssignmentOperator

    private fun getFallbackType(psiType: PsiType): IrType {
        return irTypePrimitive(psiType.presentableText).apply {
            nullable(true)
        }.build()
    }

    fun <T> Array<T>.splitWhen(predicate: (T) -> Boolean): List<List<T>> {
        val result = mutableListOf<MutableList<T>>()
        for (item in this) {
            if (predicate(item) || result.isEmpty()) {
                result.add(mutableListOf())
            }
            result.last().add(item)
        }
        return result
    }
}
