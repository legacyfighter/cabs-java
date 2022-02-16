package io.legacyfighter.cabs.contracts.legacy;

import java.util.Random;

public class Contract2 extends Document implements Versionable{

    public Contract2(String number, User creator) {
        super(number, creator);
    }

    @Override
    public void publish() throws UnsupportedTransitionException {
        throw new UnsupportedTransitionException(status, DocumentStatus.PUBLISHED);
    }

    public void accept(){
        if (status == DocumentStatus.VERIFIED){
            status = DocumentStatus.PUBLISHED; //reusing unused enum to provide data model for new status
        }
    }

    //Contracts just don't have a title, it's just a part of the content
    @Override
    public void changeTitle(String title) {
        super.changeContent(title + getContent());
    }

    //NOT @Override
    public void changeContent(String content, String userStatus){
        if (userStatus == "ChiefSalesOfficerStatus" || misterVladimirIsLoggedIn(userStatus)){
            overridePublished = true;
            changeContent(content);
        }
    }

    private boolean misterVladimirIsLoggedIn(String userStatus) {
        return userStatus.toLowerCase().trim().equals("!!!id=" + NUMBER_OF_THE_BEAST);
    }

    private static final String NUMBER_OF_THE_BEAST = "616";

    @Override
    public void recreateTo(long version) {
        //TODO need to learn Kafka
    }

    @Override
    public long getLastVersion() {
        return new Random().nextLong();//FIXME, don't know how to return a null
    }
}
