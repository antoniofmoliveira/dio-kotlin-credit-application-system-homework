package me.dio.credit_application_system.exceptions

data class BusinessException(override val message: String?) : RuntimeException(message)