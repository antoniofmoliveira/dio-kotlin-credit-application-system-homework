package me.dio.credit_application_system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit_application_system.dto.CreditDto
import me.dio.credit_application_system.dto.CustomerDto
import me.dio.credit_application_system.entity.Credit
import me.dio.credit_application_system.entity.Customer
import me.dio.credit_application_system.repository.CreditRepository
import me.dio.credit_application_system.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.Test

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditResourceTest {

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var creditRepository: CreditRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/credits"
    }

    @BeforeEach
    fun setup() {
        creditRepository.deleteAll()
        customerRepository.deleteAll()
    }

    @AfterEach
    fun tearDown(){
        creditRepository.deleteAll()
        customerRepository.deleteAll()
    }

    @Test
    fun `should create a credit and return 201 status`() {
        //given
        val customerDto: CustomerDto = builderCustomerDto()
        val savedCustomer: Customer = customerRepository.save(customerDto.toEntity())
        val creditDto: CreditDto = builderCreditDto(customerId = savedCustomer.id!!)
        val valueAsString = objectMapper.writeValueAsString(creditDto)

        //when
        //then
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(valueAsString)
            )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not create a credit with invalid customerId and return 400 status`() {
        //given
        val creditDto: CreditDto = builderCreditDto(customerId = 1L)
        val valueAsString = objectMapper.writeValueAsString(creditDto)

        //when
        //then
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(valueAsString)
            )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class me.dio.credit_application_system.exceptions.BusinessException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not create a credit with invalid dayFirstInstallment and return 400 status`() {
        //given
        val customerDto: CustomerDto = builderCustomerDto()
        val savedCustomer: Customer = customerRepository.save(customerDto.toEntity())
        val creditDto: CreditDto = builderCreditDto(customerId = savedCustomer.id!!, dayFirstInstallment = LocalDate.of(2022, 1, 1))
        val valueAsString = objectMapper.writeValueAsString(creditDto)

        //when
        //then
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(valueAsString)
            )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class org.springframework.web.bind.MethodArgumentNotValidException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should list all credits by customerId and return 200 status`() {
        //given
        val customerDto: CustomerDto = builderCustomerDto()
        val savedCustomer: Customer = customerRepository.save(customerDto.toEntity())
        val creditDto1: CreditDto = builderCreditDto(customerId = savedCustomer.id!!)
        val creditDto2: CreditDto = builderCreditDto(customerId = savedCustomer.id!!, creditValue = BigDecimal(2000.0))
        creditRepository.save(creditDto1.toEntity())
        creditRepository.save(creditDto2.toEntity())

        //when
        //then
        mockMvc
            .perform(MockMvcRequestBuilders.get("$URL?customerId=${savedCustomer.id}")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should return an empty list when customerId is invalid and return 200 status`() {
        //given
        //when
        //then
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("$URL?customerId=0")
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(0))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should return a credit by id and return 200 status`() {
        //given
        val customerDto: CustomerDto = builderCustomerDto()
        val savedCustomer: Customer = customerRepository.save(customerDto.toEntity())
        val creditDto: CreditDto = builderCreditDto(customerId = savedCustomer.id!!)
        val creditToSave = creditDto.toEntity()
        val savedCredit: Credit = creditRepository.save(creditToSave)
        //when
        //then
        mockMvc
            .perform(MockMvcRequestBuilders.get("$URL/${savedCredit.creditCode}?customerId=${savedCustomer.id}")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun `should not return a credit by id when constumerId is not linked to credit and return 4000 status`() {
        //given
        val customerDto: CustomerDto = builderCustomerDto()
        val savedCustomer: Customer = customerRepository.save(customerDto.toEntity())
        val creditDto: CreditDto = builderCreditDto(customerId = savedCustomer.id!!)
        val creditToSave = creditDto.toEntity()
        val savedCredit: Credit = creditRepository.save(creditToSave)
        //when
        //then
        mockMvc
            .perform(MockMvcRequestBuilders.get("$URL/${savedCredit.creditCode}?customerId=0")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value("class java.lang.IllegalArgumentException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andDo(MockMvcResultHandlers.print())
    }


    private fun builderCustomerDto(
        firstName: String = "Cami",
        lastName: String = "Cavalcante",
        cpf: String = "28475934625",
        email: String = "camila@gmail.com",
        income: BigDecimal = BigDecimal(1000.0),
        password: String = "1234",
        zipCode: String = "12345",
        street: String = "Rua da Cami,123"
    ) = CustomerDto(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        zipCode = zipCode,
        street = street
    )

    private fun builderCreditDto(
        creditValue: BigDecimal = BigDecimal.valueOf(1000.0),
        dayFirstInstallment: LocalDate = LocalDate.of(2025, 1, 1),
        numberOfInstallments: Int = 5,
        customerId: Long=1
    ) = CreditDto(
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        customerId= customerId
    )

}