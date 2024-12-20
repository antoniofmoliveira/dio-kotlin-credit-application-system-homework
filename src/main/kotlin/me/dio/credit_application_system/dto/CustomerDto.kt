package me.dio.credit_application_system.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import me.dio.credit_application_system.entity.Address
import me.dio.credit_application_system.entity.Customer
import org.hibernate.validator.constraints.br.CPF
import java.math.BigDecimal

data class CustomerDto(
    @field:NotEmpty(message = "Invalid first name") val firstName: String,
    @field:NotEmpty(message = "Invalid last name") val lastName: String,
    @field:NotEmpty(message = "Invalid cpf")
    @field:CPF(message = "Invalid cpf") val cpf: String,
    @field:NotNull(message = "Invalid income") val income: BigDecimal,
    @field:NotEmpty(message = "Invalid email")
    @field:Email(message = "Invalid email") val email: String,
    @field:NotEmpty(message = "Invalid password") val password: String,
    @field:NotEmpty(message = "Invalid zip code") val zipCode: String,
    @field:NotEmpty(message = "Invalid street") val street: String
) {

    fun toEntity(): Customer = Customer(
        firstName = this.firstName,
        lastName = this.lastName,
        cpf = this.cpf,
        email = this.email,
        income = this.income,
        password = this.password,
        address = Address(
            zipCode = this.zipCode,
            street = this.street
        )
    )
}