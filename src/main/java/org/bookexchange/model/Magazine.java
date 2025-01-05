package org.bookexchange.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bookexchange.dto.PublicationDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Magazine extends Publication{
    private int issueNumber;

    @Override
    public PublicationDto toDto() {
        PublicationDto dto = new PublicationDto();
        dto.setId(this.getId());
        dto.setTitle(this.getTitle());
        dto.setAuthor(this.getAuthor());
        dto.setPrice(this.getPrice());
        dto.setOwnerUsername(this.getOwner().getUsername());
        dto.setStatus(this.getStatus());
        dto.setPublicationType("Magazine");
        dto.setIssueNumber(this.issueNumber);
        return dto;
    }
}
