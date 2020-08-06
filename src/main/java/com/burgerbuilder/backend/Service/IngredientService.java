package com.burgerbuilder.backend.Service;

import com.burgerbuilder.backend.DTO.Request.IngredientRequest;
import com.burgerbuilder.backend.Exception.NotFoundException;
import com.burgerbuilder.backend.Exception.ResourceExistException;
import com.burgerbuilder.backend.Model.Ingredient;
import com.burgerbuilder.backend.Repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    @Autowired
    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }


    public ResponseEntity<?> createIngredient(IngredientRequest request) {

        checkIfIngredientExist(request.getName());

        ingredientRepository.save(new Ingredient(request.getName(),request.getPrice()));
        return new ResponseEntity<>(Map.of("status","created"),HttpStatus.CREATED);
    }

    public ResponseEntity<?> getAllIngredients() {
        return new ResponseEntity<>(ingredientRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<?> getIngredientByName(String ingredientName) {
        var ingredient= findIngredientByName(ingredientName);
        return new ResponseEntity<>(ingredient,HttpStatus.OK);
    }

    public ResponseEntity<?> updateIngredientByName(String ingredientName, IngredientRequest request) {
        var ingredient=findIngredientByName(ingredientName);
        checkIfIngredientExist(request.getName());

        if(request.getName().trim().length() > 2)
            ingredient.setName(request.getName());
        if(request.getPrice()>0.2)
            ingredient.setPrice(request.getPrice());

        ingredientRepository.updateById(ingredientName,ingredient.getName(),ingredient.getPrice());
        return new ResponseEntity<>(Map.of("status","updated"),HttpStatus.OK);
    }

    public ResponseEntity<?> deleteIngredientByName(String ingredientName){
        if(!ingredientRepository.existsById(ingredientName))
            throw new NotFoundException(404,"no ingredient with this name :"+ingredientName);

        ingredientRepository.deleteByName(ingredientName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Ingredient findIngredientByName(String ingredientName) {
        return ingredientRepository.findById(ingredientName)
                .orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND.value(),
                        "no ingredient with this name :" + ingredientName));
    }
    private void checkIfIngredientExist(String ingredientName) {
        if(ingredientRepository.existsById(ingredientName))
            throw new ResourceExistException(HttpStatus.CONFLICT.value(), "ingredient already exist");
    }
}
