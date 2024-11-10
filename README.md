AllergenService/Resource -> Put everything under BeezzerService/Resource ??  

BeezzerService/Resource:  
@GET List<BeezzerDTO> getAllBeezzers()  
@GET BeezzerDTO getBeezzer(Long beezzerId)  
@PUT void setBeezzer(Beezzer beezzer)  
@POST Beezzer addBeezzer(Beezzer beezzer)  
@DELETE boolean removeBeezzer(Long beezzerId)  
@GET LocationDTO getBeezzerLocation(Long beezzerId)
@POST addAllergen(String stringPollen, Beezzer beezzer)  
@DELETE removeAllergen(Long idAllergen, Beezzer beezzer)    
