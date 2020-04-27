package ca.mobilelive.retailstore.controller;

import ca.mobilelive.retailstore.exception.ProductNotFoundException;
import ca.mobilelive.retailstore.model.Constants;
import ca.mobilelive.retailstore.model.Product;
import ca.mobilelive.retailstore.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by Noush on 10/25/2018.
 */
@RestController
@Api(value = "Retail Store", description = "CRUD operation on a product")
public class ProductController {

    @Autowired
    ProductService productService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @PostMapping(value = "/createProduct")
    @ApiOperation(value = "Creating a product")
    @ApiResponse(code = 200, message = "OK", response = Product.class)
    public ResponseEntity<Product> createProduct(@RequestParam("name") String name,
                                                 @RequestParam("description") String description,
                                                 @RequestParam("quantity") int quantity,
                                                 @RequestParam("unitPrice") String unitPrice) {
        Product product = makeProductModel(null, name, description, quantity, unitPrice);
        Product createdProduct = productService.saveOrUpdateProduct(product);
        LOGGER.info("Product was created successfully!");
        return new ResponseEntity<Product>(createdProduct, HttpStatus.OK);
    }

    @PutMapping(value = "/updateProduct")
    @ApiOperation(value = "Updating a product")
    @ApiResponse(code = 200, message = "OK", response = Product.class)
    public ResponseEntity<Product> updateProduct(@RequestParam("id") long id,
                                                 @RequestParam("name") String name,
                                                 @RequestParam("description") String description,
                                                 @RequestParam("quantity") int quantity,
                                                 @RequestParam("unitPrice") String unitPrice) {
        Product product = makeProductModel(id, name, description, quantity, unitPrice);
        Product updatedProduct = productService.saveOrUpdateProduct(product);
        LOGGER.info("Product was saved successfully!");
        return new ResponseEntity<Product>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping(value = "/deleteProduct")
    @ApiOperation(value = "Deleting a product")
    @ApiResponses({@ApiResponse(code = 200, message = Constants.SUCCESSFUL_DELETE_MESSAGE),
            @ApiResponse(code = 400, message = "Bad Request")})
    public ResponseEntity<String> deleteProduct(@RequestParam("id") long id) {
        try {
            productService.deleteProduct(id);
        } catch (ProductNotFoundException e) {
            LOGGER.error("No product found to delete!");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        LOGGER.info("Product was deleted successfully!");
        return new ResponseEntity<String>(Constants.SUCCESSFUL_DELETE_MESSAGE, HttpStatus.OK);
    }

    @GetMapping(value = "/getProduct")
    @ApiOperation(value = "Getting a product")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = Product.class),
            @ApiResponse(code = 404, message = "Not Found")})
    public ResponseEntity<Object> getProduct(@RequestParam("id") long id) throws ProductNotFoundException {
        Optional<Product> product = productService.getProduct(id);
        if (!product.isPresent()) {
            LOGGER.info("There is no product with id {}", id);
            return new ResponseEntity<Object>(Constants.NO_PRODUCT_FOUND_MESSAGE, HttpStatus.NOT_FOUND);
        }
        LOGGER.info(product.toString());
        return new ResponseEntity<Object>(product, HttpStatus.OK);
    }

    @GetMapping(value = "/list")
    @ApiOperation(value = "Listing all products")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = Product.class),
            @ApiResponse(code = 404, message = "Not Found")})
    public ResponseEntity<Object> getProducts() throws ProductNotFoundException {
        Iterable<Product> productList = productService.getProducts();
        LOGGER.info(productList.toString());
        if (Objects.isNull(productList) || !productList.iterator().hasNext()) {
            LOGGER.info("There is no product in the database.");
            return new ResponseEntity<Object>(Constants.NO_PRODUCT_LIST_MESSAGE, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Object>(productList, HttpStatus.OK);
    }

    private Product makeProductModel(Long id, String name, String description, int quantity, String unitPrice) {
        Product product = new Product();
        if (id != null)
            product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setQuantity(quantity);
        product.setUnitPrice(unitPrice);
        LOGGER.info("Product model was created, {}", product.toString());
        return product;
    }

}
