package alexandre.blended.service;

import alexandre.blended.exception.AlreadyExistsException;
import alexandre.blended.exception.BadRequestException;
import alexandre.blended.exception.NotFoundException;
import alexandre.blended.model.Activity;
import alexandre.blended.model.Registration;
import alexandre.blended.repository.ActivityRepository;
import alexandre.blended.repository.RegistrationRepository;
import alexandre.blended.util.Utilities;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ActivityService {

    @Value("${app.backendUrl}")
    private String backendUrl;

    @Value("${app.frontendUrl}")
    private String frontendUrl;

    @Value("${app.send-mail}")
    private boolean sendMail;

    private ActivityRepository activityRepository;
    private RegistrationRepository registrationRepository;
    private EmailService emailService;

    @Autowired
    public ActivityService(ActivityRepository activityRepository, RegistrationRepository registrationRepository, EmailService emailService){
        this.activityRepository = activityRepository;
        this.registrationRepository = registrationRepository;
        this.emailService = emailService;
    }

    public List<Activity> findAll(){
        return activityRepository.findAll();
    }

    public Activity findById(String id){
        return activityRepository.findById(id).orElseThrow(() -> new NotFoundException("Activity", "id", id));
    }

    public Registration findByRegistrationId(String registrationId){
        return registrationRepository.findById(registrationId).orElseThrow(() -> new NotFoundException("Registration", "id", registrationId));
    }

    public Activity save(Activity activity){
        if(activity.getId() == null || activity.getId().length() == 0){
            activity.setId(Utilities.uuid());
        }
        activity.setDates(activity.getDates().stream().map(ActivityService::cleanDate).collect(Collectors.toList()));
        if(activityRepository.existsById(activity.getId())){
            throw new AlreadyExistsException("Activity", "id", activity.getId());
        }
        return activityRepository.save(activity);
    }

    private static String cleanDate(String date){
        try {
            return LocalDateTime.parse(date).withNano(0).toString();
        } catch (DateTimeException ex){
            throw new BadRequestException("Invalid date specified: " + date);
        }
    }

    private void checkDate(Activity activity, String date){
        if(!activity.getDates().contains(date)){
            throw new NotFoundException("Date", "value", date);
        }
    }

    public Registration register(Activity activity, Registration registration){
        checkDate(activity, registration.getDate());
        registration.setId(Utilities.uuid());
        registration.setDate(cleanDate(registration.getDate()));
        registration = registrationRepository.save(registration);
        if(sendMail){
            emailService.sendMail(registration.getEmail(), String.format("Registration for %s", activity.getName()),
                    "registration",
                    Map.of("activity", activity, "registration", registration,
                            "registrationUrl", String.format("%s/activities/%s/registrations/%s", frontendUrl, activity.getId(), registration.getId())));
        }
        return registration;
    }

    public Registration cancelRegistration(Activity activity, Registration registration){
        if(!registration.getActivityId().equals(activity.getId())){
            throw new BadRequestException("Registration is for different activity.");
        }
        if(registration.getCancelledDate() != null){
            throw new BadRequestException("Registration already cancelled.");
        }
        checkDate(activity, registration.getDate());
        registration.setCancelledDate(LocalDateTime.now().toString());
        return registrationRepository.save(registration);
    }

    public BufferedImage generateQRCode(Activity activity, String date){
        date = cleanDate(date);
        checkDate(activity, date);
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        try {
            return MatrixToImageWriter.toBufferedImage(
                    barcodeWriter.encode(String.format("%s/activities/%s/%s/register", frontendUrl, activity.getId(), date), BarcodeFormat.QR_CODE, 200, 200));
        } catch (WriterException e) {
            throw new RuntimeException(String.format("Could not generate QR code for Activity %s with name %s on date %s.\n", activity.getId(),activity.getName(), date));
        }
    }
}
