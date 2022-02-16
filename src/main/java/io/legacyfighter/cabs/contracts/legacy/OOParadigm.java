package io.legacyfighter.cabs.contracts.legacy;

abstract class OOParadigm {
    //2. enkapsulacja - ukrycie impl
    private Object filed;

    //1. abstrakcja - agent odbierający sygnały
    public void method(){
        //do sth
    }

    //3. polimorfizm - zmienne zachowania
    protected abstract void abstractStep();
}

//4. dziedziczenie - technika wspierająca polimorizm
class ConcreteType extends OOParadigm{

    @Override
    protected void abstractStep() {

    }
}
