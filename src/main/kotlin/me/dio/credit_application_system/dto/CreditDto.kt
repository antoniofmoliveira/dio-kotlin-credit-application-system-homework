package me.dio.credit_application_system.dto

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotNull
import me.dio.credit_application_system.entity.Credit
import me.dio.credit_application_system.entity.Customer
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDto(
    @field:NotNull(message = "Invalid Credit Value") val creditValue: BigDecimal,
    @field:Future val dayFirstInstallment: LocalDate,
    @field:NotNull(message = "Invalid Number of Installments") val numberOfInstallments: Int,
    @field:NotNull(message = "Invalid Customer") val customerId: Long,
) {

    fun toEntity(): Credit = Credit(
        creditValue = this.creditValue,
        dayFirstInstallment = this.dayFirstInstallment,
        numberOfInstallments = this.numberOfInstallments,
        customer = Customer(id = this.customerId)
    )
}
