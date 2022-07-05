package com.micropos.products.mapper;

import com.micropos.dto.ProductDto;
import com.micropos.products.model.Product;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

@Mapper
public interface ProductMapper {

    default Collection<ProductDto> toProductsDto(Collection<Product> products) {
        ArrayList<ProductDto> productDtos = new ArrayList<>();
        for(var product: products) {
            var productDto = this.toProductDto(product);
            productDtos.add(productDto);
        }
        return productDtos;
    }

    Collection<Product> toProducts(Collection<ProductDto> products);

    Product toProduct(ProductDto productDto);

    default ProductDto toProductDto(Product product) {
        var productDto = new ProductDto();
        productDto.id(product.id()).price(BigDecimal.valueOf(product.price())).image(product.image());
        productDto.name(product.name());
        return productDto;
    };
}
