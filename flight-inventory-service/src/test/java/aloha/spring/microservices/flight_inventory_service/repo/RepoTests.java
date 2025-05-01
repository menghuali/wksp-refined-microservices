package aloha.spring.microservices.flight_inventory_service.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import aloha.spring.microservices.flight_inventory_service.model.FlightInventory;
import aloha.spring.microservices.flight_inventory_service.model.Reservation;


@Sql(scripts = "test-data.sql")
@DataJpaTest
public class RepoTests {

    @Autowired
    private ReservationRepo rsRepo;

    @Autowired
    private FlightInventoryRepo fiRepo;

    @Test
    public void testReservationFindById() {
         Optional<Reservation> optional = rsRepo.findById(1l);
         assertNotNull(optional);
         assertTrue(optional.isPresent());
         Reservation reservation = optional.get();
         FlightInventory inventory = reservation.getFlightInventory();
         assertNotNull(inventory);
         assertEquals(1l, inventory.getId());
         assertEquals("AAA", inventory.getOrigin());
    }

    @Test
    public void testFlightInventoryFindById() {
        Optional<FlightInventory> optional = fiRepo.findById(1l);
        assertNotNull(optional);
        assertTrue(optional.isPresent());
        FlightInventory inventory = optional.get();
        assertEquals(1l, inventory.getId());
        assertEquals("AAA", inventory.getOrigin());
        
    }

}
