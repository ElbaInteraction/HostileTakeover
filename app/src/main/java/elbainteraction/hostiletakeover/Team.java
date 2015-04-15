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
    protected String teamName;

    public Team(int teamColor, String teamName){
        this.teamName = teamName;
        this.teamColor = teamColor;
    }

    public Member addMember(Member member){
        members.add(member);
        return member;
    }
    public String getTeamName(){
        return teamName;
    }
}
