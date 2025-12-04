package ar.edu.itba.paw.model.image;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "image")
@NoArgsConstructor // Hibernate needs this
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Image {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_imageid_seq")
    @SequenceGenerator(sequenceName = "image_imageid_seq", name = "image_imageid_seq", allocationSize = 1)
    @Column(name = "imageid")
    private Integer imageID;

    @Column(name = "photoblob")
    private byte[] imageBlob;

    public Image(Integer imageID, byte[] imageBlob) {
        this.imageID = imageID;
        this.imageBlob = imageBlob;
    }

    public void setImageID(Integer imageID) {
        this.imageID = imageID;
    }

    public void setImageBlob(byte[] imageBlob) {
        this.imageBlob = imageBlob;
    }
}

