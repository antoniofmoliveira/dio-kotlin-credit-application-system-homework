package me.dio.credit_application_system.controller

import jakarta.validation.Valid
import me.dio.credit_application_system.dto.CustomerDto
import me.dio.credit_application_system.dto.CustomerUpdateDto
import me.dio.credit_application_system.dto.CustomerView
import me.dio.credit_application_system.entity.Customer
import me.dio.credit_application_system.service.impl.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/customers")
class CustomerResource(
    private val customerService: CustomerService
) {



    @io.swagger.v3.oas.annotations.Operation(
        summary = "Save s a customer",
        tags = ["customers"]
    )
    @PostMapping
    fun saveCustomer(@RequestBody @Valid customerDto: CustomerDto): ResponseEntity<CustomerView> {
        val savedCustomer: Customer = this.customerService.save(customerDto.toEntity())
        return ResponseEntity
            .status(HttpStatus.CREATED)
//            .body("Customer ${savedCustomer.email} saved")
            .body(CustomerView(savedCustomer))
    }

    @io.swagger.v3.oas.annotations.Operation(
        summary = "Return a customer",
        tags = ["customers"]
    )
    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<CustomerView> {
        val customer: Customer = this.customerService.findById(id)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(CustomerView(customer))
    }

    @io.swagger.v3.oas.annotations.Operation(
        summary = "Delete a customer",
        tags = ["customers"]
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCustomer(@PathVariable id: Long) = this.customerService.delete(id)

    //    @PatchMapping("/{id}")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Updates a customer",
        tags = ["customers"]
    )
    @PatchMapping("/")
    fun updateCustomer(
        @RequestParam(value = "customerId") id: Long,
        @RequestBody @Valid customerUpdateDto: CustomerUpdateDto
    ): ResponseEntity<CustomerView> {
        val customer: Customer = this.customerService.findById(id)
        val customerToUpdate: Customer = customerUpdateDto.toEntity(customer)
        val customerUpdated: Customer = this.customerService.save(customerToUpdate)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(CustomerView(customerUpdated))
    }
}