package app.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ValidationUtil {

    @Autowired
    private  Validator validator;

    public void validate(Object object) {
        Set<ConstraintViolation<Object>> validate = validator.validate(object);

        if (validate.size() > 0) {
            throw new ConstraintViolationException(validate);
        }
    }
}
