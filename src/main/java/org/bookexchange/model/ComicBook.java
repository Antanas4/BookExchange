package org.bookexchange.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bookexchange.dto.PublicationDto;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class ComicBook extends Publication{
    private String illustrator;

    @Override
    public PublicationDto toDto() {
        PublicationDto dto = new PublicationDto();
        dto.setId(this.getId());
        dto.setTitle(this.getTitle());
        dto.setAuthor(this.getAuthor());
        dto.setPrice(this.getPrice());
        dto.setOwnerUsername(this.getOwner().getUsername());
        dto.setStatus(this.getStatus());
        dto.setPublicationType("Comic Book");
        dto.setIllustrator(this.illustrator);
        return dto;
    }
}
