package alexandre.blended.controller;

import alexandre.blended.model.Activity;
import alexandre.blended.model.Registration;
import alexandre.blended.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    private ActivityService service;

    @Autowired
    public ActivityController(ActivityService service){
        this.service = service;
    }

    @GetMapping
    public List<Activity> getActities(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Activity getActivity(@PathVariable String id){
        return service.findById(id);
    }

    @GetMapping(value = "/{activityId}/registrations/{registrationId}")
    public Registration getRegistration(@PathVariable("activityId") String activityId, @PathVariable("registrationId") String registrationId){
        return service.findByRegistrationId(registrationId);
    }

    @PostMapping(value = "/{id}/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Registration register(@PathVariable("id") String id, @RequestBody @Valid Registration registration){
        Activity activity = service.findById(id);
        return service.register(activity, registration);
    }

    @GetMapping(value = "/{id}/register/{date}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> getActivityDateQR(@PathVariable("id") String id, @PathVariable String date) {
        return ResponseEntity.ok(service.generateQRCode(service.findById(id), date));
    }

    @PostMapping
    public Activity createActivity(@RequestBody Activity activity){
        return service.save(activity);
    }

    @DeleteMapping(value = "/{activityId}/registrations/{registrationId}")
    public Registration cancelRegistration(@PathVariable("activityId") String activityId, @PathVariable("registrationId") String registrationId) {
        return service.cancelRegistration(service.findById(activityId), service.findByRegistrationId(registrationId));
    }
}
