package elbainteraction.hostiletakeover;

import android.graphics.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Henrik on 2015-04-09.
 */
public class Team {
    protected int teamColor;
    protected ArrayList<Member> members;

    public Team(int teamColor){

        this.teamColor = teamColor;
    }

    public Member addMember(Member member){
        members.add(member);
        return member;
    }
}
