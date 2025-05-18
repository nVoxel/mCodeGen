package com.voxeldev.mcodegen.dsl.ir

/**
 * Base class for all types in the IR (Intermediate Representation) system.
 * This class represents the type system used in the code generation process.
 */
interface IrType : IrElement {
    val isNullable: Boolean
}

data class IrTypeReference(
    val referencedClassName: String,
    val typeParameters: List<IrType>,
    override val isNullable: Boolean = true,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrType {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IrTypeReference

        if (isNullable != other.isNullable) return false
        if (referencedClassName != other.referencedClassName) return false
        if (typeParameters != other.typeParameters) return false
        if (annotations != other.annotations) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isNullable.hashCode()
        result = 31 * result + referencedClassName.hashCode()
        result = 31 * result + typeParameters.hashCode()
        result = 31 * result + annotations.hashCode()
        return result
    }
}

data class IrTypePrimitive(
    val primitiveType: PrimitiveType,
    override val isNullable: Boolean = true,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrType {
    interface PrimitiveType {
        open class Void : PrimitiveType {
            override fun equals(other: Any?): kotlin.Boolean = checkEquality(this, other)
        }

        open class Boolean : PrimitiveType {
            override fun equals(other: Any?): kotlin.Boolean = checkEquality(this, other)
        }

        open class Byte : PrimitiveType {
            override fun equals(other: Any?): kotlin.Boolean = checkEquality(this, other)
        }

        open class Short : PrimitiveType {
            override fun equals(other: Any?): kotlin.Boolean = checkEquality(this, other)
        }

        open class Int : PrimitiveType {
            override fun equals(other: Any?): kotlin.Boolean = checkEquality(this, other)
        }

        open class Long : PrimitiveType {
            override fun equals(other: Any?): kotlin.Boolean = checkEquality(this, other)
        }

        open class Char : PrimitiveType {
            override fun equals(other: Any?): kotlin.Boolean = checkEquality(this, other)
        }

        open class Float : PrimitiveType {
            override fun equals(other: Any?): kotlin.Boolean = checkEquality(this, other)
        }

        open class Double : PrimitiveType {
            override fun equals(other: Any?): kotlin.Boolean = checkEquality(this, other)
        }

        fun checkEquality(
            primitiveTypeOne: PrimitiveType,
            primitiveTypeTwo: Any?,
        ): kotlin.Boolean {
            if (primitiveTypeTwo == null) return false
            return primitiveTypeOne::class == primitiveTypeTwo::class
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IrTypePrimitive

        if (isNullable != other.isNullable) return false
        if (primitiveType != other.primitiveType) return false
        if (annotations != other.annotations) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isNullable.hashCode()
        result = 31 * result + primitiveType.hashCode()
        result = 31 * result + annotations.hashCode()
        return result
    }


}

/**
 * Represents a function type in the IR (Intermediate Representation) system.
 * Function types represent the signature of a function, including its parameter types and return type.
 */
data class IrTypeFunction(
    val parameterTypes: List<IrType>,
    val returnType: IrType,
    override val isNullable: Boolean = true,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrType {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IrTypeFunction

        if (isNullable != other.isNullable) return false
        if (parameterTypes != other.parameterTypes) return false
        if (returnType != other.returnType) return false
        if (annotations != other.annotations) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isNullable.hashCode()
        result = 31 * result + parameterTypes.hashCode()
        result = 31 * result + returnType.hashCode()
        result = 31 * result + annotations.hashCode()
        return result
    }
}

data class IrTypeArray(
    val elementType: IrType,
    override val isNullable: Boolean = true,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrType {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IrTypeArray

        if (isNullable != other.isNullable) return false
        if (elementType != other.elementType) return false
        if (annotations != other.annotations) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isNullable.hashCode()
        result = 31 * result + elementType.hashCode()
        result = 31 * result + annotations.hashCode()
        return result
    }
}

data class IrTypeGeneric(
    val name: String,
    override val isNullable: Boolean = true,
    override val location: IrLocation? = null,
    override val annotations: List<IrAnnotation> = emptyList(),
    override val languageProperties: Map<String, Any> = emptyMap()
) : IrType {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IrTypeGeneric

        if (isNullable != other.isNullable) return false
        if (name != other.name) return false
        if (annotations != other.annotations) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isNullable.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + annotations.hashCode()
        return result
    }
}
