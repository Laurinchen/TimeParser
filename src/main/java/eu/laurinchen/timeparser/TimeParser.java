package eu.laurinchen.timeparser;

import java.time.Duration;
import java.util.*;

/**
 * Utility class containing {@link #parse(String)}, {@link #parseWithDefault(String, char)}
 */
public final class TimeParser{
    public static final long YEAR_IN_SECONDS = 365 * 24 * 60 * 60;
    public static final long MONTH_IN_SECONDS = 30 * 24 * 60 * 60;
    public static final long WEEK_IN_SECONDS = 7 * 24 * 60 * 60;
    public static final long DAY_IN_SECONDS = 24 * 60 * 60;
    public static final long HOUR_IN_SECONDS = 60 * 60;
    public static final long MINUTE_IN_SECONDS = 60;
    public static final long SECOND = 1;
    private TimeParser(){}

    /**
     * Returns the characters which may be used as duration literals.<br/>
     * Use '\0' for {@link #parseWithDefault(String, char)}
     * to denote that no default duration literal should be used. If you know at compile time that no default duration
     * literal will be used, then {@link #parse(String)} should be used!
     */
    public static final Set<Character> VALID_DURATION_LITERALS = Set.of('y', 'M', 'w', 'd', 'h', 'm', 's', '\0');

    /**
     * Parses "toParse" in conjunction with "durationLiteral" to a {@link Duration}
     * @param durationLiteral
     * must be lowercase except 'M' for months
     * <table>
     *     <thead>
     *         <tr>
     *             <th>duration literal</th>
     *             <th>full name</th>
     *         </tr>
     *     </thead>
     *     <tbody>
     *         <tr>
     *             <td>y</td>
     *             <td>year(s)</td>
     *         </tr>
     *             <td>M</td>
     *             <td>month(s)</td>
     *         </tr>
     *         </tr>
     *             <td>w</td>
     *             <td>week(s)</td>
     *         </tr>
     *         </tr>
     *             <td>d</td>
     *             <td>day(s)</td>
     *         </tr>
     *         </tr>
     *             <td>h</td>
     *             <td>hour(s)</td>
     *         </tr>
     *         </tr>
     *             <td>m</td>
     *             <td>minute(s)</td>
     *             <td>toParse * 60</td>
     *         </tr>
     *         </tr>
     *             <td>s</td>
     *             <td>second(s)</td>
     *         </tr>
     *     </tbody>
     * </table>
     * @param toParse integer literal as a {@link String}. If it's empty, 1 will be used in its place
     * @return parsed {@link Duration}
     * @throws IllegalArgumentException if an invalid duration literal is used
     * @throws NumberFormatException if toParse can't be parsed by {@link Integer#parseInt(String)}<br>
     */
    public static Duration parseSingle(final char durationLiteral, String toParse) throws IllegalArgumentException,
            NumberFormatException{
        if(toParse.isEmpty()){
            toParse = "1";
        }
        final long parsedNumber = Integer.parseInt(toParse);
        return switch (durationLiteral) {
            case 'y' -> Duration.ofSeconds(parsedNumber * YEAR_IN_SECONDS);
            case 'M' -> Duration.ofSeconds(parsedNumber * MONTH_IN_SECONDS);
            case 'w' -> Duration.ofSeconds(parsedNumber * WEEK_IN_SECONDS);
            case 'd' -> Duration.ofSeconds(parsedNumber * DAY_IN_SECONDS);
            case 'h' -> Duration.ofSeconds(parsedNumber * HOUR_IN_SECONDS);
            case 'm' -> Duration.ofSeconds(parsedNumber * MINUTE_IN_SECONDS);
            case 's' -> Duration.ofSeconds(parsedNumber * SECOND);
            default -> throw new IllegalArgumentException(
                    String.format("'%c' is not a known duration literal", durationLiteral)
            );
        };
    }

    /**
     * Parses a {@link String} with integer literals and duration literals to a {@link Duration}
     * @param toParse {@link String} to parse. <br>
     * Examples of allowed forms:
     * <ul>
     *     <li>"30s" //1 integer literal and 1 duration literal</li>
     *     <li>"5m30s" //several integer literals and several duration literals</li>
     *     <li>"y" //just a duration literal</li>
     *     <li>"500" //just a integer literal (used in conjunction with "defaultDurationLiteral")</li>
     * </ul>
     * Examples of forbidden uses:
     * <ul>
     *     <li>"50" //bare integer literal when "defaultDurationLiteral" is '\0'</li>
     *     <li>"123d60" //an integer literal without a duration literal when at least 1 other duration literal is present</li>
     *     <li>"10w20w</li> //reuse of duration literals
     *     <li>"15a"</li> //unknown duration literals
     * </ul>
     * Allowed duration literals (must be lowercase except 'M' for months):
     * <table>
     *     <thead>
     *         <tr>
     *             <th>duration literal</th>
     *             <th>full name</th>
     *         </tr>
     *     </thead>
     *     <tbody>
     *         <tr>
     *             <td>y</td>
     *             <td>year(s)</td>
     *         </tr>
     *             <td>M</td>
     *             <td>month(s)</td>
     *         </tr>
     *         </tr>
     *             <td>w</td>
     *             <td>week(s)</td>
     *         </tr>
     *         </tr>
     *             <td>d</td>
     *             <td>day(s)</td>
     *         </tr>
     *         </tr>
     *             <td>h</td>
     *             <td>hour(s)</td>
     *         </tr>
     *         </tr>
     *             <td>m</td>
     *             <td>minute(s)</td>
     *         </tr>
     *         </tr>
     *             <td>s</td>
     *             <td>second(s)</td>
     *         </tr>
     *     </tbody>
     * </table>
     * @param defaultDurationLiteral If "toParse" is just a bare integer literal, this duration literal will then be used.
     * If it should throw an exception
     * instead, then '\0' should be used.<br>
     * If it's known at compile-time that this will be '\0', then {{@link #parse(String)}} should be used instead.
     * @return
     * parsed {@link Duration}
     * @throws IllegalArgumentException if an unknown (default) duration literal has been found, "toParse" is empty,
     * a duration literal is used more than once, "defaultDurationLiteral" is '\0' and "toParse" is just a bare integer or
     * if "toParse" contains an integer literal without a duration literal when at least 1 other duration literal is given
     * @throws NumberFormatException if an integer literal in "toParse" couldn't be parsed<br>
     */
    public static Duration parseWithDefault(final String toParse, final char defaultDurationLiteral)
            throws IllegalArgumentException{
        if(!VALID_DURATION_LITERALS.contains(defaultDurationLiteral)){
            throw new IllegalArgumentException(
                    String.format("unknown default duration literal %c", defaultDurationLiteral)
            );
        }
        if(toParse.isEmpty()){
            throw new IllegalArgumentException("toParse can't be empty");
        }

        Duration result = Duration.ZERO;

        StringBuilder buffer = new StringBuilder();
        Set<Character> durationLiteralsAlreadyUsed = new HashSet<>();
        for(char c: toParse.toCharArray()){
            if(Character.isDigit(c)){
                buffer.append(c);
                continue;
            }
           if(durationLiteralsAlreadyUsed.contains(c)){
               throw new IllegalArgumentException(String.format("duration literal '%c' used more than once", c));
           }
           durationLiteralsAlreadyUsed.add(c);
           result = result.plus(parseSingle(c, buffer.toString()));
           buffer.setLength(0);
        }

        if(!buffer.isEmpty()){
            if(!durationLiteralsAlreadyUsed.isEmpty()){
                throw new IllegalArgumentException(String.format("time with no duration literal given: %s",
                        buffer
                ));
            }
            if(defaultDurationLiteral == '\0'){
                throw new IllegalArgumentException(
                        String.format("single time with no duration literal or default duration given: %s", buffer
                ));
            }
            result = result.plus(parseSingle(defaultDurationLiteral, buffer.toString()));
        }

        return result;
    }
    /**
     * (Calls {@link #parseWithDefault(String, char)} where "defaultDurationLiteral" is '\0')<br>
     * Parses a {@link String} with integer literals and duration literals to a {@link Duration}
     * @param toParse {@link String} to parse. <br>
     * Examples of allowed forms:
     * <ul>
     *     <li>"30s" //1 integer literal and 1 duration literal</li>
     *     <li>"5m30s" //several integer literals and several duration literals</li>
     *     <li>"y" //just a duration literal</li>
     * </ul>
     * Examples of forbidden uses:
     * <ul>
     *     <li>"50" //bare integer literal (Consider using {@link #parseWithDefault(String, char)} instead)</li>
     *     <li>"123d60" //an integer literal without a duration literal</li>
     *     <li>"10w20w</li> //reuse of duration literals
     *     <li>"15a"</li> //unknown duration literals
     * </ul>
     * Allowed duration literals (must be lowercase except 'M' for months):
     * <table>
     *     <thead>
     *         <tr>
     *             <th>duration literal</th>
     *             <th>full name</th>
     *         </tr>
     *     </thead>
     *     <tbody>
     *         <tr>
     *             <td>y</td>
     *             <td>year(s)</td>
     *         </tr>
     *             <td>M</td>
     *             <td>month(s)</td>
     *         </tr>
     *         </tr>
     *             <td>w</td>
     *             <td>week(s)</td>
     *         </tr>
     *         </tr>
     *             <td>d</td>
     *             <td>day(s)</td>
     *         </tr>
     *         </tr>
     *             <td>h</td>
     *             <td>hour(s)</td>
     *         </tr>
     *         </tr>
     *             <td>m</td>
     *             <td>minute(s)</td>
     *         </tr>
     *         </tr>
     *             <td>s</td>
     *             <td>second(s)</td>
     *         </tr>
     *     </tbody>
     * </table>
     * @return parsed {@link Duration}
     * @throws IllegalArgumentException "toParse" is empty, a duration literal is used more than once,"toParse" is or
     * contains a bare integer
     * @throws NumberFormatException if an integer literal in "toParse" couldn't be parsed
     */
    public static Duration parse(final String toParse){
        return parseWithDefault(toParse, '\0');
    }
}
