package com.voxeldev.mcodegen.dsl.language.kotlin.source.parse.extensions

import com.voxeldev.mcodegen.dsl.ir.IrClass
import com.voxeldev.mcodegen.dsl.ir.IrClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrAnnotationClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrClassClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrEnumClassKind
import com.voxeldev.mcodegen.dsl.ir.IrClassKind.IrInterfaceClassKind
import com.voxeldev.mcodegen.dsl.ir.IrSuperClass
import com.voxeldev.mcodegen.dsl.ir.IrTypeReference
import com.voxeldev.mcodegen.dsl.ir.builders.irClass
import com.voxeldev.mcodegen.dsl.ir.builders.irParameter
import com.voxeldev.mcodegen.dsl.ir.builders.irSuperClass
import com.voxeldev.mcodegen.dsl.language.kotlin.KotlinModule
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.IrObjectClassKind
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.kotlinInternalVisibility
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.kotlinPrivateVisibility
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.kotlinProtectedVisibility
import com.voxeldev.mcodegen.dsl.language.kotlin.ir.kotlinPublicVisibility
import com.voxeldev.mcodegen.dsl.scenario.ScenarioScope
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassNotAny
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces

const val KT_SUPERCLASS_CTOR_PARAMETERS = "ktSuperClassCtorParameters"

context(KotlinModule, BindingContext, ScenarioScope)
internal fun convertClass(ktClassOrObject: KtClassOrObject): IrClass {
    val classSimpleName = ktClassOrObject.name ?: "Ir:UnnamedClass"
    val classQualifiedName = ktClassOrObject.fqName?.asString() ?: "Ir:UnnamedClass"
    val classDescriptor = this@BindingContext.get(BindingContext.CLASS, ktClassOrObject)
        ?: throw IllegalArgumentException("Unable to get KtClass descriptor through BindingContext")

    val irClassBuilder = irClass(
        qualifiedName = classQualifiedName,
        simpleName = classSimpleName,
    )

    irClassBuilder.kind(
        when {
            ktClassOrObject is KtObjectDeclaration -> IrObjectClassKind
            ktClassOrObject is KtClass && ktClassOrObject.isAnnotation() -> IrAnnotationClassKind
            ktClassOrObject is KtClass && ktClassOrObject.isEnum() -> IrEnumClassKind
            ktClassOrObject is KtClass && ktClassOrObject.isInterface() -> IrInterfaceClassKind
            else -> IrClassClassKind
        }
    )

    irClassBuilder.visibility(
        when {
            ktClassOrObject.hasModifier(KtTokens.PROTECTED_KEYWORD) -> kotlinProtectedVisibility()
            ktClassOrObject.hasModifier(KtTokens.INTERNAL_KEYWORD) -> kotlinInternalVisibility()
            ktClassOrObject.hasModifier(KtTokens.PRIVATE_KEYWORD) -> kotlinPrivateVisibility()
            else -> kotlinPublicVisibility()
        }
    )

    if (ktClassOrObject.hasModifier(KtTokens.ABSTRACT_KEYWORD)) {
        irClassBuilder.addLanguageProperty(
            KtTokens.ABSTRACT_KEYWORD.value, true
        )
    }

    if (ktClassOrObject.hasModifier(KtTokens.COMPANION_KEYWORD)) {
        irClassBuilder.addLanguageProperty(
            KtTokens.COMPANION_KEYWORD.value, true
        )
    }

    if (ktClassOrObject.hasModifier(KtTokens.DATA_KEYWORD)) {
        irClassBuilder.addLanguageProperty(
            KtTokens.DATA_KEYWORD.value, true
        )
    }

    if (ktClassOrObject.hasModifier(KtTokens.FINAL_KEYWORD)) {
        irClassBuilder.addLanguageProperty(
            KtTokens.FINAL_KEYWORD.value, true
        )
    }

    if (ktClassOrObject.hasModifier(KtTokens.OPEN_KEYWORD)) {
        irClassBuilder.addLanguageProperty(
            KtTokens.OPEN_KEYWORD.value, true
        )
    }

    if (ktClassOrObject.hasModifier(KtTokens.INNER_KEYWORD)) {
        irClassBuilder.addLanguageProperty(
            KtTokens.INNER_KEYWORD.value, true
        )
    }

    if (ktClassOrObject.hasModifier(KtTokens.SEALED_KEYWORD)) {
        irClassBuilder.addLanguageProperty(
            KtTokens.SEALED_KEYWORD.value, true
        )
    }

    ktClassOrObject.annotationEntries.forEach { ktAnnotationEntry ->
        irClassBuilder.addAnnotation(convertAnnotation(ktClassOrObject, ktAnnotationEntry))
    }

    convertTypeParameters(ktClassOrObject, ktClassOrObject.typeParameters, irClassBuilder)

    val superClass = classDescriptor.getSuperClassNotAny()?.let { superClassDescriptor ->
        convertSuperclass(ktClassOrObject, superClassDescriptor)
    }

    superClass?.let { irClassBuilder.addSuperClass(superClass) }

    val interfaces = classDescriptor.getSuperInterfaces().mapNotNull { superInterfaceDescriptor ->
        convertSuperclass(ktClassOrObject, superInterfaceDescriptor)
    }

    interfaces.forEach { superInterface -> irClassBuilder.addSuperClass(superInterface) }

    // : SuperClass(params)
    val superClassConstructorParams = ktClassOrObject.superTypeListEntries
        .filterIsInstance<KtSuperTypeCallEntry>()
        .firstOrNull()
        ?.let { call ->
            val resolvedCall = call.getResolvedCall(this@BindingContext) ?: return@let null
            val preloadedTypeParameters = preloadTypeParameters(ktClassOrObject.typeParameters)

            resolvedCall.valueArguments.mapNotNull { (param, arg) ->
                val expression = arg.arguments.single().getArgumentExpression() ?: return@mapNotNull null
                val type = this@BindingContext.getType(expression) ?: return@mapNotNull null
                val name = param.name.asString()

                irParameter(name, convertKotlinType(type, preloadedTypeParameters)).apply {
                    defaultValue(convertExpression(ktClassOrObject, expression))
                }.build()
            }
        }

    superClassConstructorParams?.let {
        irClassBuilder.addLanguageProperty(
            KT_SUPERCLASS_CTOR_PARAMETERS,
            superClassConstructorParams,
        )
    }

    // Convert fields
    convertFieldsAsProperties(
        ktClassOrObject = ktClassOrObject,
        fields = ktClassOrObject.declarations.filterIsInstance<KtProperty>(),
        irClassBuilder = irClassBuilder,
    )

    // Convert methods
    convertFunctions(
        ktClassOrObject = ktClassOrObject,
        functions = buildList {
            addAll(ktClassOrObject.declarations.filterIsInstance<KtFunction>())
            ktClassOrObject.primaryConstructor?.let { primaryConstructor ->
                add(primaryConstructor)
            }
            // addAll(ktClassOrObject.secondaryConstructors) they should also present in declarations
        },
        irClassBuilder = irClassBuilder,
    )

    // Convert initializers
    convertInitializers(
        initializers = ktClassOrObject.declarations.filterIsInstance<KtClassInitializer>(),
        irClassBuilder = irClassBuilder,
    )

    ktClassOrObject.declarations.filterIsInstance<KtClassOrObject>().forEach { nestedClass ->
        irClassBuilder.addNestedClass(convertClass(nestedClass))
    }

    return irClassBuilder.build()
}

context(KotlinModule, BindingContext, ScenarioScope)
private fun convertSuperclass(
    ktClassOrObject: KtClassOrObject,
    classDescriptor: ClassDescriptor,
): IrSuperClass? {
    val fqName = classDescriptor.fqNameSafe.asString()
    if (ktClassOrObject.isAnnotation() && fqName == "kotlin.Annotation") {
        return null
    }

    return createSuperClassFromSuper(
        ktClassOrObject = ktClassOrObject,
        superClassName = fqName,
        superClassKind = if (classDescriptor.kind == ClassKind.INTERFACE) IrInterfaceClassKind else IrClassClassKind
    )
}

context(KotlinModule, BindingContext, ScenarioScope)
private fun createSuperClassFromSuper(
    ktClassOrObject: KtClassOrObject,
    superClassName: String,
    superClassKind: IrClassKind,
): IrSuperClass {
    val irSuperClassBuilder = irSuperClass(superClassName, superClassKind)

    val preloadedTypeParameters = preloadTypeParameters(ktClassOrObject.typeParameters)
    val superAsType = ktClassOrObject.superTypeListEntries
        .mapNotNull { superTypeListEntry -> superTypeListEntry.typeReference?.typeElement }
        .mapNotNull { superTypeElement ->
            convertKtTypeElement(
                superTypeElement,
                preloadedTypeParameters
            ) as? IrTypeReference
        }
        .find { superTypeReference -> superTypeReference.referencedClassName == superClassName }
        ?: throw IllegalArgumentException("Unable to find superclass in ktClassOrObject by name")

    superAsType.typeParameters.forEach { typeParameter ->
        irSuperClassBuilder.addType(typeParameter)
    }

    return irSuperClassBuilder.build()
}
