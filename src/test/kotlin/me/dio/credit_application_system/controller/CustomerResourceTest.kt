package me.dio.credit_application_system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit_application_system.dto.CustomerDto
import me.dio.credit_application_system.dto.CustomerUpdateDto
import me.dio.credit_application_system.entity.Customer
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
import kotlin.test.Test


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CustomerResourceTest {

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/customers"
    }

    @BeforeEach
    fun setup() = customerRepository.deleteAll()

    @AfterEach
    fun tearDown() = customerRepository.deleteAll()

    @Test
    fun `should create a customer and return 201 status`() {
        //given
        val customerDto: CustomerDto = builderCustomerDto()
        val valueAsString = objectMapper.writeValueAsString(customerDto)

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
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(customerDto.firstName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(customerDto.lastName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value(customerDto.cpf))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(customerDto.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value(customerDto.zipCode))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value(customerDto.street))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a customer with same CPF and return 409 status`() {
        //given
        val customerDto: CustomerDto = builderCustomerDto()
        customerRepository.save(customerDto.toEntity())
        val valueAsString = objectMapper.writeValueAsString(customerDto)

        //when
        //then
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post(URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(valueAsString)
            )
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Conflict! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("class org.springframework.dao.DataIntegrityViolationException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a customer with empty firstName and return 400 status`() {
        //given
        val customerDto: CustomerDto = builderCustomerDto(firstName = "")
        val valueAsString = objectMapper.writeValueAsString(customerDto)

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
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("class org.springframework.web.bind.MethodArgumentNotValidException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find a customer by id and return 200 status`() {
        //given
        val customerDto: CustomerDto = builderCustomerDto()
        val savedCustomer: Customer = customerRepository.save(customerDto.toEntity())

        //when
        //then
        mockMvc
            .perform(MockMvcRequestBuilders.get("$URL/${savedCustomer.id}")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(savedCustomer.firstName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(savedCustomer.lastName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value(savedCustomer.cpf))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(savedCustomer.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value(savedCustomer.address.zipCode))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value(savedCustomer.address.street))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedCustomer.id))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find a customer by id and return 404 status`() {
        //given
        val id: Long = 1L

        //when
        //then
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("$URL/$id")
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("class me.dio.credit_application_system.exceptions.BusinessException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun `should delete a customer by id and return 204 status`() {
        //given
        val customerDto: CustomerDto = builderCustomerDto()
        val savedCustomer: Customer = customerRepository.save(customerDto.toEntity())

        //when
        //then
        mockMvc
            .perform(MockMvcRequestBuilders.delete("$URL/${savedCustomer.id}")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not delete a customer with non existent id and return 400 status`() {
        //given
        val id: Long = 1L
        //when
        //then
        mockMvc
            .perform(MockMvcRequestBuilders.delete("$URL/${id}")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("class me.dio.credit_application_system.exceptions.BusinessException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should update a customer by id and return 200 status`() {
        //given
        val customerDto: CustomerDto = builderCustomerDto()
        val savedCustomer: Customer = customerRepository.save(customerDto.toEntity())
        val alteredCustomerDto: CustomerUpdateDto = builderCustomerUpdateDto()
        val valueAsString = objectMapper.writeValueAsString(alteredCustomerDto)

        //when
        //then
        mockMvc
            .perform(MockMvcRequestBuilders.patch("$URL/?customerId=${savedCustomer.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(alteredCustomerDto.firstName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(alteredCustomerDto.lastName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value(alteredCustomerDto.income))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value(alteredCustomerDto.zipCode))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value(alteredCustomerDto.street))
            .andDo(MockMvcResultHandlers.print())
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not update a customer with non existent id and return 400 status`() {
        //given
        val id: Long = 1L
        val customerDto: CustomerUpdateDto = builderCustomerUpdateDto()
        val valueAsString = objectMapper.writeValueAsString(customerDto)

        //when
        //then
        mockMvc
            .perform(
                MockMvcRequestBuilders.patch("$URL/?customerId=${id}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(valueAsString)
            )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.title")
                .value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception")
                .value("class me.dio.credit_application_system.exceptions.BusinessException"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
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

    private fun builderCustomerUpdateDto(
        firstName: String = "Camila",
        lastName: String = "Cavalcantete",
        income: BigDecimal = BigDecimal(2000.0),
        zipCode: String = "123456",
        street: String = "Rua da Cami,1236"
    ) = CustomerUpdateDto(
        firstName = firstName,
        lastName = lastName,
        income = income,
        zipCode = zipCode,
        street = street
    )
}