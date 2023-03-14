import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;
import ru.netology.patient.service.medical.MedicalService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.verify;

public class MedicalServiceImplTests {
    static PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
    SendAlertService alertService = Mockito.mock(SendAlertServiceImpl.class);
    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);


    @BeforeAll
    public static void AddMock() {
        Mockito.when(patientInfoFileRepository.getById("123"))
                .thenReturn(new PatientInfo("123",
                        "Anna",
                        "Ivanova",
                        LocalDate.of(1980, 11, 26),
                        new HealthInfo(new BigDecimal("38.5"), new BloodPressure(140, 100))));
    }

    @Test
    public void TestCheckBloodPressure() {
        MedicalService medicalService = new MedicalServiceImpl(patientInfoFileRepository, alertService);
        medicalService.checkBloodPressure("123", new BloodPressure(120, 80));

        verify(alertService).send(argumentCaptor.capture());
        Assertions.assertEquals("Warning, patient with id: 123, need help", argumentCaptor.getValue());
    }

    @Test
    public void TestCheckTemperature() {
        MedicalService medicalService = new MedicalServiceImpl(patientInfoFileRepository, alertService);
        medicalService.checkTemperature("123", new BigDecimal("36.6"));

        verify(alertService).send(argumentCaptor.capture());
        Assertions.assertEquals("Warning, patient with id: 123, need help", argumentCaptor.getValue());
    }

    @Test
    public void TestNormalHealth() {
        MedicalService medicalService = new MedicalServiceImpl(patientInfoFileRepository, alertService);
        medicalService.checkBloodPressure("123", new BloodPressure(140, 100));
        medicalService.checkTemperature("123", new BigDecimal("38.5"));

        verify(alertService, Mockito.times(0)).send("Warning, patient with id: 123, need help");
    }
}
