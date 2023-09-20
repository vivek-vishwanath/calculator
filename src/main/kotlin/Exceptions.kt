class ImproperExpressionException(override val message: String?): RuntimeException(message)

class IncompatibleUnitsException(override val message: String?): RuntimeException(message)

class UnknownSymbolException(override val message: String?): RuntimeException(message)