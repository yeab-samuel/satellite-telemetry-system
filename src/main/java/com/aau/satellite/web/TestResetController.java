package com.aau.satellite.web;

import com.aau.satellite.repository.MissionRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@Profile({"test", "dev"})   // only loads in test/dev profiles
public class TestResetController {

    private final MissionRepository missionRepository;

    public TestResetController(MissionRepository missionRepository) {
        this.missionRepository = missionRepository;
    }

    @DeleteMapping("/reset")
    public ResponseEntity<Void> reset() {
        missionRepository.deleteAll();
        return ResponseEntity.ok().build();
    }
}