package com.smartcare.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "medical_reports")
public class MedicalReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "appointment_id", nullable = false)
    private Long appointmentId;

    @Column(name = "prescription_id")
    private Long prescriptionId;

    @NotNull
    @Size(min = 3, max = 150)
    @Column(nullable = false, length = 150)
    private String title;

    @NotNull
    @Size(min = 3, max = 80)
    @Column(name = "report_type", nullable = false, length = 80)
    private String reportType;

    @NotNull
    @Column(name = "report_date", nullable = false)
    private LocalDateTime reportDate;

    @Size(max = 2000)
    @Column(length = 2000)
    private String notes;

    @Size(max = 2048)
    @Pattern(regexp = "^https://.+", message = "fileUrl must use HTTPS")
    @Column(name = "file_url", length = 2048)
    private String fileUrl;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
    public Long getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(Long prescriptionId) { this.prescriptionId = prescriptionId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public LocalDateTime getReportDate() { return reportDate; }
    public void setReportDate(LocalDateTime reportDate) { this.reportDate = reportDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
