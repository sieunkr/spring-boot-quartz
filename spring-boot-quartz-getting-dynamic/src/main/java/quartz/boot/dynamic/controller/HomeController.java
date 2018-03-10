package quartz.boot.dynamic.controller;

import lombok.RequiredArgsConstructor;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quartz.boot.dynamic.model.JobDescriptor;
import quartz.boot.dynamic.service.CoffeeService;

import java.util.Collection;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final CoffeeService coffeeService;

    @PostMapping(path = "/groups/{group}/jobs")
    public ResponseEntity<JobDescriptor> createJob(@PathVariable String group, @RequestBody JobDescriptor descriptor) {

        return new ResponseEntity<>(coffeeService.createJob(group, descriptor), CREATED);
    }

    @GetMapping(path = "/groups/{group}/jobs/list")
    public Collection<Optional<JobDescriptor>> listJob(@PathVariable String group) {
        return coffeeService.listJob(group);
    }

    @GetMapping(path = "/groups/{group}/jobs/{name}")
    public ResponseEntity<JobDescriptor> findJob(@PathVariable String group, @PathVariable String name) {
        return coffeeService.findJob(group, name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/groups/{group}/jobs/{name}")
    public ResponseEntity<Void> updateJob(@PathVariable String group, @PathVariable String name, @RequestBody JobDescriptor descriptor) {
        coffeeService.updateJob(group, name, descriptor);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/groups/{group}/jobs/{name}")
    public ResponseEntity<Void> deleteJob(@PathVariable String group, @PathVariable String name) {
        coffeeService.deleteJob(group, name);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/groups/{group}/jobs/{name}/pause")
    public ResponseEntity<Void> pauseJob(@PathVariable String group, @PathVariable String name) {
        coffeeService.pauseJob(group, name);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/groups/{group}/jobs/{name}/resume")
    public ResponseEntity<Void> resumeJob(@PathVariable String group, @PathVariable String name) {
        coffeeService.resumeJob(group, name);
        return ResponseEntity.noContent().build();
    }


}
