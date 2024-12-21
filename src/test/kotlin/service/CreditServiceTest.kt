package service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import me.dio.credit_application_system.entity.Address
import me.dio.credit_application_system.entity.Credit
import me.dio.credit_application_system.entity.Customer
import me.dio.credit_application_system.entity.enummeration.Status
import me.dio.credit_application_system.exceptions.BusinessException
import me.dio.credit_application_system.repository.CreditRepository
//import me.dio.credit_application_system.repository.CustomerRepository
import me.dio.credit_application_system.service.impl.CreditService
import me.dio.credit_application_system.service.impl.CustomerService
import org.aspectj.apache.bcel.classfile.Code
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import kotlin.test.Test

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CreditServiceTest {


//    @MockK
//    lateinit var customerRepository: CustomerRepository

    @MockK
    @InjectMockKs
    lateinit var customerService: CustomerService

    @MockK
    lateinit var creditRepository: CreditRepository

    @InjectMockKs
    lateinit var creditService: CreditService

    @Test
    fun `should create credit`() {
        // given
        val fakeId: Long = 1
        val fakeCustomer = buildCustomer(id = fakeId)
        every { customerService.findById(fakeCustomer.id!!) } returns fakeCustomer
        val fakeCredit = buildCredit(customer = fakeCustomer)
        every { creditRepository.save(fakeCredit) } returns fakeCredit

        // when
        val actual = creditService.save(fakeCredit)

        // then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCredit)
        verify(exactly = 1) { creditRepository.save(fakeCredit) }
    }

    @Test
    fun `should return a list of credit`(){
        //given
        val fakeId: Long = 1
        val fakeCustomer = buildCustomer(id = fakeId)
        val fakeCredits:List<Credit> = listOf (buildCredit(customer = fakeCustomer), buildCredit(customer = fakeCustomer))
        every {creditRepository.findAllByCustomerId(fakeId)} returns fakeCredits

        //when
        val actual =creditService.findAllByCustomer(fakeId)

        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isNotEmpty()
        Assertions.assertThat(actual).hasSize(2)
        Assertions.assertThat(actual).isSameAs(fakeCredits)
        verify(exactly = 1) { creditRepository.findAllByCustomerId(fakeId) }
    }

    @Test
    fun `should find by credit code`() {
        // given
        val fakeId: Long = 1
        val fakeCustomer = buildCustomer(id = fakeId)
        val fakeCreditCode = UUID.randomUUID()
        val fakeCredit = buildCredit(creditCode = fakeCreditCode, customer = fakeCustomer)
        every { creditRepository.findByCreditCode( fakeCreditCode) } returns fakeCredit

        // when
        val actual = creditService.findByCreditCode(fakeId, fakeCreditCode)

        // then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCredit)
        verify(exactly = 1) { creditRepository.findByCreditCode( fakeCreditCode) }
    }

    @Test
    fun `should not find by credit code an throw BusinessException`() {
        // given
        val fakeId: Long = 1
        val anotherFakeId : Long = 2
        val fakeCustomer = buildCustomer(id = fakeId)
        val fakeCreditCode = UUID.randomUUID()
        val anotherFakeCreditCode = UUID.randomUUID()
        val fakeCredit = buildCredit(creditCode = fakeCreditCode, customer = fakeCustomer)
        every { creditRepository.findByCreditCode( fakeCreditCode) } returns fakeCredit
        every { creditRepository.findByCreditCode( anotherFakeCreditCode) } returns null


        // when
        // then
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy { creditService.findByCreditCode(anotherFakeId, fakeCreditCode)}
            .withMessage("Contact admin")
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { creditService.findByCreditCode(fakeId, anotherFakeCreditCode)}
            .withMessage("Credit code $anotherFakeCreditCode not found")
    }

    private fun buildCustomer(
        id: Long = 1L,
        firstName: String = "Cami",
        lastName: String = "Cavalcante",
        cpf: String = "28475934625",
        email: String = "camila@gmail.com",
        password: String = "12345",
        zipCode: String = "12345",
        street: String = "Rua da Cami",
        income: BigDecimal = BigDecimal.valueOf(1000.0)
    ) = Customer(
        id = id,
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        address = Address(
            zipCode = zipCode,
            street = street
        ),
        income = income
    )

    private fun buildCredit(
        creditCode: UUID = UUID.randomUUID(),
        creditValue: BigDecimal = BigDecimal.valueOf(1000.0),
        dayFirstInstallment: LocalDate = LocalDate.now(),
        numberOfInstallments: Int = 5,
        customer: Customer = buildCustomer(),
        status: Status = Status.IN_PROGRESS
    ) = Credit(
        creditCode=creditCode  ,
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        status = status,
        customer = customer
    )
}