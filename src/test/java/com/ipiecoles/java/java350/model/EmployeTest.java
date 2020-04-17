package com.ipiecoles.java.java350.model;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

public class EmployeTest {

    @Test
    public void getNombreAnneeAncienneteNow() throws EmployeException {
        //Given
        Employe e = new Employe();
        e.setDateEmbauche(LocalDate.now());

        //When
        Integer anneeAnciennete = e.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(anneeAnciennete.intValue()).isEqualTo(0);
    }

    @Test
    public void getNombreAnneeAncienneteNminus2() throws EmployeException {
        //Given
        Employe e = new Employe();
        e.setDateEmbauche(LocalDate.now().minusYears(2L));

        //When
        Integer anneeAnciennete = e.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(anneeAnciennete.intValue()).isEqualTo(2);
    }

    @Test
    public void getNombreAnneeAncienneteNull() throws EmployeException {
        //Given
        Employe e = new Employe();
        e.setDateEmbauche(null);

        Assertions.assertThatThrownBy(() -> {
            //When
            Integer anneeAnciennete = e.getNombreAnneeAnciennete();
        })
            //Then
            .isInstanceOf(Exception.class)
            .hasMessage("La date d'embauche est null");
    }

    @Test
    public void getNombreAnneeAncienneteNplus2() throws EmployeException {
        //Given
        Employe e = new Employe();
        e.setDateEmbauche(LocalDate.now().plusYears(2L));

        //When
        Integer anneeAnciennete = e.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(anneeAnciennete.intValue()).isEqualTo(-2);
    }

    @ParameterizedTest
    @CsvSource({
            "1, 'T12345', 0, 1.0, 1000.0",
            "1, 'T12345', 2, 0.5, 600.0",
            "1, 'T12345', 2, 1.0, 1200.0",
            "2, 'T12345', 0, 1.0, 2300.0",
            "2, 'T12345', 1, 1.0, 2400.0",
            "1, 'M12345', 0, 1.0, 1700.0",
            "1, 'M12345', 5, 1.0, 2200.0",
            "2, 'M12345', 0, 1.0, 1700.0",
            "2, 'M12345', 8, 1.0, 2500.0"
    })
    public void getPrimeAnnuelle(Integer performance, String matricule, Long nbYearsAnciennete, Double tempsPartiel, Double primeAnnuelle) throws EmployeException {
        //Given
        Employe employe = new Employe("Doe", "John", matricule, LocalDate.now().minusYears(nbYearsAnciennete), Entreprise.SALAIRE_BASE, performance, tempsPartiel);

        //When
        Double prime = employe.getPrimeAnnuelle();

        //Then
        Assertions.assertThat(primeAnnuelle).isEqualTo(prime);

    }


    @Test
    public void augmenterSalaire10() throws EmployeException {
        //Given
        Employe e = new Employe("Doe","John","C00001",LocalDate.now(),1000.0,2,2.0);
        Double pourcentage = 10.00;

        //When
        e.augmenterSalaire(pourcentage);

        //Then
        Assertions.assertThat(e.getSalaire()).isEqualTo(1100);
    }


    @Test
    public void augmenterSalaireMinus10() throws EmployeException {
        //Given
        Employe e = new Employe("Doe","John","C00001",LocalDate.now(),1000.0,2,2.0);
        Double pourcentage = -10.00;

        Assertions.assertThatThrownBy(() -> {
            //When
            e.augmenterSalaire(pourcentage);
        })
            //Then
            .isInstanceOf(Exception.class)
            .hasMessage("Le pourcentage d'augmentation ne peut pas etre negatif");

    }

    @Test
    public void augmenterSalaire0() throws EmployeException {
        //Given
        Employe e = new Employe("Doe","John","C00001",LocalDate.now(),1000.0,2,2.0);
        Double pourcentage = 0.00;

        //When
        e.augmenterSalaire(pourcentage);

        //Then
        Assertions.assertThat(e.getSalaire()).isEqualTo(1000);
    }


    @Test
    public void augmenterSalaireNull() throws EmployeException {
        //Given
        Employe e = new Employe("Doe","John","C00001",LocalDate.now(),null,2,2.0);
        Double pourcentage = 10.00;

        Assertions.assertThatThrownBy(() -> {
            //When
            e.augmenterSalaire(pourcentage);
        })
            //Then
            .isInstanceOf(Exception.class)
            .hasMessage("Le salaire est null");
    }


    @Test
    public void getNombreCongesTest() throws EmployeException {
        //Given
        Employe e = new Employe();
        e.setDateEmbauche(LocalDate.now());

        //When
        Integer nombreConges = e.getNbConges();

        //Then
        Assertions.assertThat(nombreConges.intValue()).isEqualTo(25);
    }


    @Test
    public void getNombreRTTNow() throws EmployeException {
        //Given
        Employe e = new Employe();
        e.setDateEmbauche(LocalDate.now());

        //When
        Integer nombreRTT = e.getNbRtt();

        //Then
        Assertions.assertThat(nombreRTT.intValue()).isEqualTo(9);
    }



    

}