/**
 * All collection type values supported by David are here. There is currently only one collection typed value that
 * David returns: DTable. But it has internal structure. It is composeed of:
 * <ol>
 *     <li>DHeader</li>
 *     <li>DBody</li>
 * </ol>
 * DHeader is a list of DAttributes and DBody is a list of DRows.
 * The user is given access only to DTable and DRow.
 */
package rojares.sling.typed_value.collection;