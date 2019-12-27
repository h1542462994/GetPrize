package core;

public class Pair<TK,TV>  {
    public Pair(TK key, TV value){
        this.key = key;
        this.value = value;
    }

    public TK getKey(){
        return this.key;
    }

    public TV getValue(){
        return this.value;
    }

    @Override
    public String toString() {
        return this.key + "=" + this.value;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o){
            return true;
        } else if(!(o instanceof Pair)){
            return false;
        } else {
            Pair pair = (Pair)o;
            if(this.key != null){
                if (!this.key.equals(pair.key)){
                    return false;
                }
            } else if(pair.key != null){
                return false;
            }

            if(this.value != null){
                if(!this.value.equals(pair.value)){
                    return false;
                }
            } else if(pair.value != null){
                return false;
            }

            return true;
        }
    }

    private TK key;
    private TV value;

}
