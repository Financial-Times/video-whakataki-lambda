package com.ft.whakataki.lambda.thing;


import com.ft.whakataki.lambda.thing.exception.ThingBadSearchRequestException;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class GeoThingLambdaTest {


    private GeoThingLambda geoThingLambda;

    @Before
    public void setup() {
        geoThingLambda = new GeoThingLambda();
    }

    @Test
    public void checkShortSearchStringThrowsBadSearchException() {
        String searchString = "xxx";
        String compactSearchString = geoThingLambda.validSearchString(searchString);
        assert(compactSearchString).equals(searchString);
    }

    @Test(expected = ThingBadSearchRequestException.class)
    public void checkShortSearchStringWithWildCardThrowsBadSearchException() {
        geoThingLambda.validSearchString("xx*");
    }

    @Test(expected = ThingBadSearchRequestException.class)
    public void checkSSearchStringWithTwoildCardsThrowsBadSearchException() {
        geoThingLambda.validSearchString("te*t*");
    }

    @Test(expected = ThingBadSearchRequestException.class)
    public void checkSearchStringWithMultipleWildCardsThrowsBadSearchException() {
        geoThingLambda.validSearchString("te*t*ng* hello");
    }

    @Test(expected = ThingBadSearchRequestException.class)
    public void checkShortSearchDueToWhiteSpaceThrowsBadSearchException() {
        geoThingLambda.validSearchString("       *");
    }

    @Test(expected = ThingBadSearchRequestException.class)
    public void checkShortSearchDueToWhiteSpaceCheckTwoThrowsBadSearchException() {
        geoThingLambda.validSearchString("       a*");
    }

    @Test(expected = ThingBadSearchRequestException.class)
    public void checkShortSearchDueToWhiteSpaceCheckThreeThrowsBadSearchException() {
        geoThingLambda.validSearchString("a       *");
    }

    @Test(expected = ThingBadSearchRequestException.class)
    public void checkShortSearchDueToWhiteSpaceCheckFourThrowsBadSearchException() {
        geoThingLambda.validSearchString("abcd   *");
    }

    @Test(expected = ThingBadSearchRequestException.class)
    public void checkShortSearchDueToWhiteSpaceCheckFiveThrowsBadSearchException() {
        geoThingLambda.validSearchString("a   b*");
    }

    @Test(expected = ThingBadSearchRequestException.class)
    public void checkShortSearchDueToWhiteSpaceCheckSixThrowsBadSearchException() {
        geoThingLambda.validSearchString("a   bg*");
    }

    @Test(expected = ThingBadSearchRequestException.class)
    public void checkShortSearchDueToWhiteSpaceCheckSevenThrowsBadSearchException() {
        geoThingLambda.validSearchString("a   bgd*     asdd  a*");
    }

    @Test
    public void checkSearchCompaction() {
        String searchString = "abcd   abb*";
        String compactSearchString = geoThingLambda.validSearchString(searchString);
        assert(compactSearchString).equals("abcd abb*");
    }

    @Test
    public void checkSearchCompactionTwo() {
        String searchString = "Southwark  Bri*";
        String compactSearchString = geoThingLambda.validSearchString(searchString);
        assert(compactSearchString).equals("Southwark Bri*");
    }

    @Test
    public void checkSearchOK() {
        String searchString = "Chelsea";
        String compactSearchString = geoThingLambda.validSearchString(searchString);
        assert(compactSearchString).equals(searchString);
    }

    @Test
    public void checkSearchOKTwo() {
        String searchString = "Chelsea*";
        String compactSearchString = geoThingLambda.validSearchString(searchString);
        assert(compactSearchString).equals(searchString);
    }

    @Test
    public void checkSearchOKThree() {
        String searchString = "Walton-on-Thames";
        String compactSearchString = geoThingLambda.validSearchString(searchString);
        assert(compactSearchString).equals(searchString);
    }

    @Test
    public void checkSearchOKFour() {
        String searchString = "Las Vegas";
        String compactSearchString = geoThingLambda.validSearchString(searchString);
        assert(compactSearchString).equals(searchString);
    }

    @Test
    public void checkSearchOKFive() {
        String searchString = "'St. Hel*";
        String compactSearchString = geoThingLambda.validSearchString(searchString);
        assert(compactSearchString).equals(searchString);
    }


    @Test
    public void checkSearchOKSix() {
        String searchString = "st. hel*";
        String compactSearchString = geoThingLambda.validSearchString(searchString);
        assert(compactSearchString).equals(searchString);
    }

    @Test
    public void checkSearchOKSeve() {
        String searchString = "c";
        String compactSearchString = geoThingLambda.validSearchString(searchString);
        assert(compactSearchString).equals(searchString);
    }

    @Test
    public void checkSearchOKEight() {
        String searchString = "westward ho";
        String compactSearchString = geoThingLambda.validSearchString(searchString);
        assert(compactSearchString).equals(searchString);
    }

    @Test
    public void checkSearchOKNine() {
        String searchString = "    a";
        String compactSearchString = geoThingLambda.validSearchString(searchString);
        assert(compactSearchString).equals("a");
    }

    @Test
    public void checkSearchOKTen() {
        String searchString = "a     ";
        String compactSearchString = geoThingLambda.validSearchString(searchString);
        assert(compactSearchString).equals("a");
    }

    @Test
    public void checkSearchOKEleven() {
        String searchString = "abc   def  ghi     ";
        String compactSearchString = geoThingLambda.validSearchString(searchString);
        assert(compactSearchString).equals("abc def ghi");
    }


}
