package com.sang.sourcepattern.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PetCreationRequest {
    String name;
    String species;
    String breed;
    String gender;
    String color;
    String avatar;
    boolean sterilized;
    float weight;
    LocalDate dob;
    String healthNote;
    String favoriteFood;
    String allergies;
    String hobbies;
    String walkTime;
    List<PetMealDTO> nutritionPlan;
    int ownerId;

    List<PetDocumentRequest> initialDocuments;
    List<PetMedicalRecordDTO> medicalRecords;
    List<PetVaccinationDTO> vaccinations;
    List<PetReminderDTO> reminders;
    List<PetImageDTO> album;
}
