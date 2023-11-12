package com.community.weare.Models.factories;

import com.community.weare.Models.*;
import com.community.weare.Models.dto.PersonalProfileDTO;
import com.community.weare.Repositories.CityRepository;
import com.community.weare.Repositories.LocationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static com.community.weare.Models.factories.FactoryUtils.getNotNull;

@Component
public class PersonalProfileFactory {
    private final LocationRepository locationRepository;
    private final CityRepository cityRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PersonalProfileFactory(LocationRepository locationRepository, CityRepository cityRepository, ModelMapper modelMapper) {
        this.locationRepository = locationRepository;
        this.cityRepository = cityRepository;
        this.modelMapper = modelMapper;
    }


    public Location createLocation(City city) {
        Location location = new Location();
        location.setCity(city);
        locationRepository.saveAndFlush(location);
        return location;
    }

    public PersonalProfile covertDTOtoPersonalProfile(PersonalProfileDTO personalProfileDTO) {
        PersonalProfile personalProfile = modelMapper.map(personalProfileDTO, PersonalProfile.class);
        personalProfile.setBirthYear(personalProfileDTO.getBirthday());
        Location location = new Location();
        location.setCity(personalProfileDTO.getCity());
        personalProfile.setLocation(location);
        Sex sex = Sex.valueOf(personalProfileDTO.getSex().toUpperCase());
        personalProfile.setSex(sex);
        return personalProfile;
    }

    public PersonalProfile mergePersonalProfiles(PersonalProfile personalProfileNew, PersonalProfile personalProfileOld) {
        if (personalProfileOld.getLocation() != null) {
            personalProfileOld.getLocation().setCity(getNotNull(personalProfileNew.getLocation().getCity(),
                    personalProfileOld.getLocation().getCity()));
        } else if (personalProfileNew.getLocation().getCity() != null) {
            Location location = createLocation(personalProfileNew.getLocation().getCity());
            personalProfileOld.setLocation(location);
        }
        personalProfileOld.setPicturePrivacy(personalProfileNew.isPicturePrivacy());
        personalProfileOld.setFirstName(getNotNull(personalProfileNew.getFirstName(), personalProfileOld.getFirstName()));
        personalProfileOld.setBirthYear(getNotNull(personalProfileNew.getBirthYear(), personalProfileOld.getBirthYear()));
        personalProfileOld.setLastName(getNotNull(personalProfileNew.getLastName(), personalProfileOld.getLastName()));
        personalProfileOld.setPersonalReview(getNotNull(personalProfileNew.getPersonalReview(), personalProfileOld.getPersonalReview()));
        personalProfileOld.setSex(getNotNull(personalProfileNew.getSex(), personalProfileOld.getSex()));
        return personalProfileOld;
    }
}
