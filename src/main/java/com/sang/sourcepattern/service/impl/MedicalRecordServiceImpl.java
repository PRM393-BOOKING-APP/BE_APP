package com.sang.sourcepattern.service.impl;

import com.sang.sourcepattern.dto.request.PetMedicalRecordDTO;
import com.sang.sourcepattern.dto.request.PetVaccinationDTO;
import com.sang.sourcepattern.dto.response.PetMedicalRecordResponse;
import com.sang.sourcepattern.dto.response.PetVaccinationResponse;
import com.sang.sourcepattern.entity.Booking;
import com.sang.sourcepattern.entity.PetMedicalRecord;
import com.sang.sourcepattern.entity.PetVaccination;
import com.sang.sourcepattern.entity.Staff;
import com.sang.sourcepattern.entity.User;
import com.sang.sourcepattern.exception.AppException;
import com.sang.sourcepattern.exception.ErrorCode;
import com.sang.sourcepattern.repository.BookingRepository;
import com.sang.sourcepattern.repository.PetMedicalRecordRepository;
import com.sang.sourcepattern.repository.PetVaccinationRepository;
import com.sang.sourcepattern.repository.StaffRepository;
import com.sang.sourcepattern.repository.UserRepository;
import com.sang.sourcepattern.service.MedicalRecordService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MedicalRecordServiceImpl implements MedicalRecordService {

    PetMedicalRecordRepository medicalRecordRepository;
    PetVaccinationRepository vaccinationRepository;
    BookingRepository bookingRepository;
    UserRepository userRepository;
    StaffRepository staffRepository;

    /**
     * Resolves the acting Staff for a medical entry. A hired Staff member
     * always wins. If the caller isn't Staff, but is the SHOP_OWNER of the
     * shop this booking belongs to, they're allowed to record entries too
     * (returns null — staff is nullable — and callers fall back to the
     * booking's shop for display purposes). Anyone else is rejected.
     */
    private Staff resolveStaffOrShopOwner(String userEmail, Booking booking) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Optional<Staff> staffOpt = staffRepository.findByUser(user);
        if (staffOpt.isPresent()) {
            return staffOpt.get();
        }

        boolean isShopOwner = booking.getShop() != null
                && booking.getShop().getOwner() != null
                && booking.getShop().getOwner().getId() == user.getId();
        if (isShopOwner) {
            return null;
        }

        throw new AppException(ErrorCode.STAFF_NOT_FOUND);
    }

    @Override
    public PetMedicalRecordResponse addMedicalRecord(int bookingId, PetMedicalRecordDTO request, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        Staff staff = resolveStaffOrShopOwner(userEmail, booking);

        PetMedicalRecord record = PetMedicalRecord.builder()
                .pet(booking.getPet())
                .booking(booking)
                .staff(staff)
                .diagnosis(request.getDiagnosis())
                .treatment(request.getTreatment())
                .prescription(request.getPrescription())
                .visitDate(request.getVisitDate() != null ? request.getVisitDate() : java.time.LocalDateTime.now())
                .veterinarianNote(request.getVeterinarianNote())
                .build();

        record = medicalRecordRepository.save(record);
        return toMedicalResponse(record);
    }

    @Override
    public PetVaccinationResponse addVaccination(int bookingId, PetVaccinationDTO request, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        Staff staff = resolveStaffOrShopOwner(userEmail, booking);
        String clinicName = staff != null && staff.getShop() != null
                ? staff.getShop().getShopName()
                : (booking.getShop() != null ? booking.getShop().getShopName() : null);

        PetVaccination vaccination = PetVaccination.builder()
                .pet(booking.getPet())
                .booking(booking)
                .staff(staff)
                .name(request.getName())
                .drug(request.getDrug())
                .clinic(clinicName)
                .date(request.getDate() != null ? request.getDate() : java.time.LocalDateTime.now())
                .status(request.getStatus() != null ? request.getStatus() : "done")
                .build();

        vaccination = vaccinationRepository.save(vaccination);
        return toVaccinationResponse(vaccination);
    }

    @Override
    public List<PetMedicalRecordResponse> getMedicalRecordsByPet(int petId) {
        return medicalRecordRepository.findByPetIdOrderByVisitDateDesc(petId).stream()
                .map(this::toMedicalResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PetMedicalRecordResponse> getMedicalRecordsByBooking(int bookingId) {
        return medicalRecordRepository.findByBookingIdOrderByVisitDateDesc(bookingId).stream()
                .map(this::toMedicalResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PetVaccinationResponse> getVaccinationsByPet(int petId) {
        return vaccinationRepository.findByPetIdOrderByDateDesc(petId).stream()
                .map(this::toVaccinationResponse)
                .collect(Collectors.toList());
    }

    private PetMedicalRecordResponse toMedicalResponse(PetMedicalRecord record) {
        return PetMedicalRecordResponse.builder()
                .id(record.getId())
                .petId(record.getPet().getId())
                .bookingId(record.getBooking() != null ? record.getBooking().getId() : null)
                .staffId(record.getStaff() != null ? record.getStaff().getId() : null)
                .staffName(record.getStaff() != null ? record.getStaff().getFullName() : null)
                .shopName(record.getStaff() != null && record.getStaff().getShop() != null ? record.getStaff().getShop().getShopName() : null)
                .diagnosis(record.getDiagnosis())
                .treatment(record.getTreatment())
                .prescription(record.getPrescription())
                .visitDate(record.getVisitDate())
                .veterinarianNote(record.getVeterinarianNote())
                .build();
    }

    private PetVaccinationResponse toVaccinationResponse(PetVaccination v) {
        return PetVaccinationResponse.builder()
                .id(v.getId())
                .petId(v.getPet().getId())
                .bookingId(v.getBooking() != null ? v.getBooking().getId() : null)
                .staffId(v.getStaff() != null ? v.getStaff().getId() : null)
                .staffName(v.getStaff() != null ? v.getStaff().getFullName() : null)
                .shopName(v.getStaff() != null && v.getStaff().getShop() != null ? v.getStaff().getShop().getShopName() : null)
                .name(v.getName())
                .drug(v.getDrug())
                .clinic(v.getClinic())
                .date(v.getDate())
                .status(v.getStatus())
                .build();
    }
}
