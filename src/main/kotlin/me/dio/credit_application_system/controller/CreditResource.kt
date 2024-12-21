package me.dio.credit_application_system.controller

import jakarta.validation.Valid
import me.dio.credit_application_system.dto.CreditDto
import me.dio.credit_application_system.dto.CreditView
import me.dio.credit_application_system.dto.CreditViewList
import me.dio.credit_application_system.entity.Credit
import me.dio.credit_application_system.service.impl.CreditService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/credits")
class CreditResource(
    private val creditService: CreditService
) {

    @io.swagger.v3.oas.annotations.Operation(
        summary = "Saves a credit",
        tags = ["credits"]
    )
    @PostMapping
    fun saveCredit(@RequestBody @Valid creditDto: CreditDto): ResponseEntity<String> {
        val credit: Credit = this.creditService.save(creditDto.toEntity())
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("Credit ${credit.creditCode} - Customer ${credit.customer?.firstName} saved")
    }

    @io.swagger.v3.oas.annotations.Operation(
        summary = "Return all credits of a customer",
        tags = ["customers", "credits"]
    )
    @GetMapping
    fun findAllByCustomerId(@RequestParam(value = "customerId") customerId: Long): ResponseEntity<List<CreditViewList>> {
        val creditViewList: List<CreditViewList> = this.creditService.findAllByCustomer(customerId).stream()
            .map { credit: Credit -> CreditViewList(credit) }
            .collect((Collectors.toList()))
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(creditViewList)
    }

    @io.swagger.v3.oas.annotations.Operation(
        summary = "Return a credit of a customer",
        tags = ["customers", "credits"]
    )
    @GetMapping("/{creditCode}")
    fun findByCreditCode(
        @RequestParam(value = "customerId") customerId: Long,
        @PathVariable(value = "creditCode") creditCode: UUID
    ): ResponseEntity<CreditView> {
        val credit: Credit = this.creditService.findByCreditCode(customerId, creditCode)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(CreditView(credit))
    }

}