package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import com.ipiecoles.java.java350.service.EmployeService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeServiceTest {

    @InjectMocks
    EmployeService employeService;

    @Mock
    EmployeRepository employeRepository;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this.getClass());
    }

    @Test
    public void testEmbaucheEmployeTechnicienPleinTempsBts() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        when(employeRepository.findLastMatricule()).thenReturn("00345");
        when(employeRepository.findByMatricule("T00346")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertThat(nom).isEqualTo(employeArgumentCaptor.getValue().getNom());
        Assertions.assertThat(prenom).isEqualTo(employeArgumentCaptor.getValue().getPrenom());
        Assertions.assertThat(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))).isEqualTo(employeArgumentCaptor.getValue().getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertThat("T00346").isEqualTo(employeArgumentCaptor.getValue().getMatricule());
        Assertions.assertThat(tempsPartiel).isEqualTo(employeArgumentCaptor.getValue().getTempsPartiel());

        //1521.22 * 1.2 * 1.0
        Assertions.assertThat(1825.46).isEqualTo(employeArgumentCaptor.getValue().getSalaire().doubleValue());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMaster() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn("00345");
        when(employeRepository.findByMatricule("M00346")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());

        Assertions.assertThat(nom).isEqualTo(employeArgumentCaptor.getValue().getNom());
        Assertions.assertThat(prenom).isEqualTo(employeArgumentCaptor.getValue().getPrenom());
        Assertions.assertThat(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))).isEqualTo(employeArgumentCaptor.getValue().getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertThat("M00346").isEqualTo(employeArgumentCaptor.getValue().getMatricule());
        Assertions.assertThat(tempsPartiel).isEqualTo(employeArgumentCaptor.getValue().getTempsPartiel());

        //1521.22 * 1.4 * 0.5
        Assertions.assertThat(1064.85).isEqualTo(employeArgumentCaptor.getValue().getSalaire().doubleValue());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMasterNoLastMatricule() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn(null);
        when(employeRepository.findByMatricule("M00001")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertThat("M00001").isEqualTo(employeArgumentCaptor.getValue().getMatricule());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMasterExistingEmploye(){
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn(null);
        when(employeRepository.findByMatricule("M00001")).thenReturn(new Employe());

        Assertions.assertThatThrownBy(() -> {
            //When
            employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
        })
            //Then
            .isInstanceOf(EntityExistsException.class)
            .hasMessage("L'employé de matricule M00001 existe déjà en BDD");
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMaster99999(){
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn("99999");

        Assertions.assertThatThrownBy(() -> {
            //When
            employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
        })
                //Then
                .isInstanceOf(EmployeException.class)
                .hasMessage("Limite des 100000 matricules atteinte !");
    }


    @Test
    public void testCalculPerformanceCommercialMatriculeDontExist() {
        //Given
        String matricule = "C00001";
        Long caTraite = 30L;
        Long objectifCa = 12345L;
        try{
            //When
            employeService.calculPerformanceCommercial(matricule,caTraite,objectifCa);
            Assertions.fail("Exception attendu");
        } catch(EmployeException e){
            //Then
            org.assertj.core.api.Assertions.assertThat(e).isInstanceOf(EmployeException.class);
            org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("Le matricule C00001 n'existe pas !");
        }
    }


    @Test
    public void testCalculPerformanceCommercialChiffreAffaireNegatif() {
        //Given
        String matricule = "C00001";
        Long caTraite = -30L;
        Long objectifCa = 12345L;
        try{
            //When
            employeService.calculPerformanceCommercial(matricule,caTraite,objectifCa);
            Assertions.fail("Exception attendu");
        } catch(EmployeException e){
            //Then
            org.assertj.core.api.Assertions.assertThat(e).isInstanceOf(EmployeException.class);
            org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("Le chiffre d'affaire traité ne peut être négatif ou null !");
        }
    }

    @Test
    public void testCalculPerformanceCommercialChiffreAffaireNull() {
        //Given
        String matricule = "C00001";
        Long caTraite = null;
        Long objectifCa = 12345L;
        try{
            //When
            employeService.calculPerformanceCommercial(matricule,caTraite,objectifCa);
            Assertions.fail("Exception attendu");
        } catch(EmployeException e){
            //Then
            org.assertj.core.api.Assertions.assertThat(e).isInstanceOf(EmployeException.class);
            org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("Le chiffre d'affaire traité ne peut être négatif ou null !");
        }
    }

    @Test
    public void testCalculPerformanceCommercialObjectifChiffreAffaireNegatif() {
        //Given
        String matricule = "C00001";
        Long caTraite = 30L;
        Long objectifCa = -12345L;
        try{
            //When
            employeService.calculPerformanceCommercial(matricule,caTraite,objectifCa);
            Assertions.fail("Exception attendu");
        } catch(EmployeException e){
            //Then
            org.assertj.core.api.Assertions.assertThat(e).isInstanceOf(EmployeException.class);
            org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("L'objectif de chiffre d'affaire ne peut être négatif ou null !");
        }
    }

    @Test
    public void testCalculPerformanceCommercialObjectifChiffreAffaireNull() {
        //Given
        String matricule = "C00001";
        Long caTraite = 30L;
        Long objectifCa = null;
        try{
            //When
            employeService.calculPerformanceCommercial(matricule,caTraite,objectifCa);
            Assertions.fail("Exception attendu");
        } catch(EmployeException e){
            //Then
            org.assertj.core.api.Assertions.assertThat(e).isInstanceOf(EmployeException.class);
            org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("L'objectif de chiffre d'affaire ne peut être négatif ou null !");
        }
    }

    @Test
    public void testCalculPerformanceCommercialMatriculeStartWithoutC() {
        //Given
        String matricule = "B00001";
        Long caTraite = 30L;
        Long objectifCa = 12345L;
        try{
            //When
            employeService.calculPerformanceCommercial(matricule,caTraite,objectifCa);
            Assertions.fail("Exception attendu");
        } catch(EmployeException e){
            //Then
            org.assertj.core.api.Assertions.assertThat(e).isInstanceOf(EmployeException.class);
            org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("Le matricule ne peut être null et doit commencer par un C !");
        }
    }

    @Test
    public void testCalculPerformanceCommercialMatriculeNull() {
        //Given
        String matricule = null;
        Long caTraite = 30L;
        Long objectifCa = 12345L;
        try{
            //When
            employeService.calculPerformanceCommercial(matricule,caTraite,objectifCa);
            Assertions.fail("Exception attendu");
        } catch(EmployeException e){
            //Then
            org.assertj.core.api.Assertions.assertThat(e).isInstanceOf(EmployeException.class);
            org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("Le matricule ne peut être null et doit commencer par un C !");
        }
    }



}