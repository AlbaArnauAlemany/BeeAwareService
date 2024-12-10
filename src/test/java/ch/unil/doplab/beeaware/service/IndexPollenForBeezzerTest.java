//package ch.unil.doplab.beeaware.service;
//
//import ch.unil.doplab.beeaware.Domain.DTO.PollenInfoDTO;
//import ch.unil.doplab.beeaware.Domain.Beezzer;
//import ch.unil.doplab.beeaware.Domain.Location;
//import ch.unil.doplab.beeaware.Domain.Pollen;
//import ch.unil.doplab.beeaware.Domain.PollenLocationIndex;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//class IndexPollenForBeezzerTest {
//
//    @Mock
//    private BeezzerService beezzerService;
//
//    @Mock
//    private PollenLocationIndexService pollenLocationIndexService;
//
//    @InjectMocks
//    private IndexPollenForBeezzer indexPollenForBeezzer;
//
//    private Beezzer beezzer;
//    private Location ecublens, nyon;
//    private PollenLocationIndex pollenLocationIndex;
//    private Pollen pollen;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        // Define Locations
//        ecublens = new Location(1040, "CH");
//        nyon = new Location(1260, "CH");
//
//        // Mock a Beezzer in Ecublens
//        beezzer = new Beezzer();
//        beezzer.setId(1L);
//        beezzer.setLocation(ecublens);
//
//        // Mock pollen and associate it with the Beezzer
//        pollen = mock(Pollen.class);
//        when(pollen.getPollenNameEN()).thenReturn("PollenA_EN");
//        beezzer.setAllergens(Map.of(1L, pollen)); // Ensure this is set after mocking
//
//        // Mock a PollenLocationIndex at Ecublens
//        pollenLocationIndex = new PollenLocationIndex();
//        pollenLocationIndex.setId(1L);
//        pollenLocationIndex.setLocation(ecublens);
//        pollenLocationIndex.setDisplayName("PollenA_EN");
//        pollenLocationIndex.setDate(new Date());
//
//        // Stubbing the services
//        when(beezzerService.getBeezzers()).thenReturn(Map.of(1L, beezzer));
//        when(pollenLocationIndexService.getPollenLocationIndexMap()).thenReturn(Map.of(1L, pollenLocationIndex));
//    }
//
//    @Test
//    void testPollenInfoDTOListWithMatchingLocation() {
//        // Call method
//        List<PollenInfoDTO> result = indexPollenForBeezzer.getIndex(beezzer.getId());
//
//        // Verify interactions
//        verify(beezzerService, times(1)).getBeezzers();
//        verify(pollenLocationIndexService, times(1)).getPollenLocationIndexMap();
//
//        // Assertions
//        assertEquals(1, result.size()); // Expect one match
//        assertEquals(pollenLocationIndex.getDisplayName(), result.get(0).getDisplayName());
//        assertEquals(ecublens.getNPA(), result.get(0).getNPA()); // Ensure that NPA matches for Ecublens
//    }
//
//    @Test
//    void testPollenInfoDTOListWithNonMatchingLocation() {
//        // Change Beezzer location to Nyon
//        beezzer.setLocation(nyon);
//
//        // Call method
//        List<PollenInfoDTO> result = indexPollenForBeezzer.getIndex(beezzer.getId());
//
//        // Verify interactions
//        verify(beezzerService, times(1)).getBeezzers();
//        verify(pollenLocationIndexService, times(1)).getPollenLocationIndexMap();
//
//        // Assertions
//        assertEquals(0, result.size()); // No matching pollen data since locations differ
//    }
//
//    @Test
//    void testPollenInfoDTOListWithMultipleLocations() {
//        // Add another PollenLocationIndex at a different location (Nyon)
//        PollenLocationIndex nyonPollenIndex = new PollenLocationIndex();
//        nyonPollenIndex.setId(2L);
//        nyonPollenIndex.setLocation(nyon);
//        nyonPollenIndex.setDisplayName("PollenB_EN");
//        nyonPollenIndex.setDate(new Date());
//
//        // Update stubbing to include both locations
//        when(pollenLocationIndexService.getPollenLocationIndexMap()).thenReturn(
//                Map.of(1L, pollenLocationIndex, 2L, nyonPollenIndex)
//        );
//
//        // Call method
//        List<PollenInfoDTO> result = indexPollenForBeezzer.getIndex(beezzer.getId());
//
//        // Verify interactions
//        verify(beezzerService, times(1)).getBeezzers();
//        verify(pollenLocationIndexService, times(1)).getPollenLocationIndexMap();
//
//        // Assertions
//        assertEquals(1, result.size()); // Only Ecublens matches
//        assertEquals(pollenLocationIndex.getDisplayName(), result.get(0).getDisplayName());
//        assertEquals(ecublens.getNPA(), result.get(0).getNPA()); // Ensure that the location NPA is correct for Ecublens
//    }
//}