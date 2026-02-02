package com.yowyob.template.domain.ports.out;

import com.yowyob.template.domain.model.Vehicle;
import com.yowyob.template.domain.model.VehicleIllustrationImage;
import com.yowyob.template.domain.model.VehicleSimplified;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VehicleRepositoryPort {
    Flux<VehicleIllustrationImage> getImages(UUID vehicleId, String jwtToken);
    
    Mono<VehicleIllustrationImage> addImage(UUID vehicleId, FilePart file, String jwtToken);
    
    Mono<Void> deleteImage(UUID imageId, String jwtToken);
    
    Mono<Vehicle> getVehicle(UUID id, String jwtToken);
    
    Mono<Vehicle> updateVehicle(UUID id, Vehicle vehicle, String jwtToken); // PATCH
    
    Mono<Vehicle> replaceVehicle(UUID id, Vehicle vehicle, String jwtToken); // PUT

    Mono<Void> deleteVehicle(UUID id, String jwtToken);
    
    Flux<Vehicle> getMyVehicles(String jwtToken);
    
    Mono<Vehicle> createVehicle(Vehicle vehicle, String jwtToken);
    
    Mono<Vehicle> createVehicleSimplified(VehicleSimplified vehicleSimplified, String jwtToken);
}
