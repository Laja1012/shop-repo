package de.shop.kundenverwaltung.domain;

import static de.shop.kundenverwaltung.domain.AbstractKunde.PRIVATKUNDE;
import static javax.persistence.FetchType.EAGER;

import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.UniqueConstraint;


@Entity
@DiscriminatorValue(PRIVATKUNDE)
@Cacheable
public class Privatkunde extends AbstractKunde {
	private static final long serialVersionUID = 133152931415808605L;
	
	@Column(name = "familienstand_fk")
	private FamilienstandType familienstand = FamilienstandType.VERHEIRATET;
	
	@Column(name = "geschlecht_fk")
	private GeschlechtType geschlecht = GeschlechtType.WEIBLICH;
	
	@ElementCollection(fetch = EAGER)
	@CollectionTable(name = "kunde_hobby",
	                 joinColumns = @JoinColumn(name = "kunde_fk", nullable = false),
	                 uniqueConstraints =  @UniqueConstraint(columnNames = { "kunde_fk", "hobby_fk" }))
	@Column(table = "kunde_hobby", name = "hobby_fk", nullable = false)
	private Set<HobbyType> hobbies;

	public FamilienstandType getFamilienstand() {
		return familienstand;
	}
	public void setFamilienstand(FamilienstandType familienstand) {
		this.familienstand = familienstand;
	}

	public GeschlechtType getGeschlecht() {
		return geschlecht;
	}
	public void setGeschlecht(GeschlechtType geschlecht) {
		this.geschlecht = geschlecht;
	}
	public Set<HobbyType> getHobbies() {
		return hobbies;
	}
	
	public void setHobbies(Set<HobbyType> hobbies) {
		this.hobbies = hobbies;
	}

	@Override
	public String toString() {
		return "Privatkunde [" + super.toString() + ", familienstand=" + familienstand
			   + ", geschlecht=" + geschlecht + ", hobbies=" + hobbies + ']';
	}
}
