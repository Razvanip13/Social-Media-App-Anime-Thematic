package socialnetwork.domain.validators;

//import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import socialnetwork.domain.users.Utilizator;
import socialnetwork.repository.paging.pagingRepo.PagingUsersRepo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UtilizatorValidator implements Validator<Utilizator> {

    PagingUsersRepo<Long,Utilizator> repo;


    public UtilizatorValidator(PagingUsersRepo<Long, Utilizator> repo) {
        this.repo = repo;
    }

    @Override
    public void validate(Utilizator entity) throws ValidationException {
        //TODO: implement method validate

        boolean cleaned=true;
        String mesajul="";


        if(repo.findOne(entity.getUsername())!=null){

            cleaned=false;

            mesajul+="This username already exists\n";

        }


        Pattern pattern=Pattern.compile("[a-zA-Z]*");
        Matcher m;




        if(entity.getFirstName().equals("")){

            cleaned=false;
            mesajul=mesajul+"Introduce the first name.\n";
        }

        m=pattern.matcher(entity.getFirstName());

        if(!m.matches()){

            cleaned=false;
            mesajul=mesajul+"First name is invalid.\n";
        }

        if(entity.getLastName().equals("")){

            cleaned=false;
            mesajul=mesajul+"Introduce the last name.\n";
        }

        m=pattern.matcher(entity.getLastName());

        if(!m.matches()){

            cleaned=false;
            mesajul=mesajul+"Last name is invalid.\n";
        }


        if(entity.getUsername().equals("")){

            cleaned=false;
            mesajul+="The username cannot be empty.\n";
        }

        if(entity.getPassword().equals("d41d8cd98f00b204e9800998ecf8427e")){

            cleaned=false;
            mesajul+="The password cannot be empty.\n";
        }

        if(!entity.getPassword().equals(entity.getConfirmPassword())){

            cleaned=false;
            mesajul+="The passwords do not match.\n";

        }

        if(!cleaned)
            throw new ValidationException(mesajul);

    }


}
