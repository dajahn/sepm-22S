export class DurationUtil {
  public static stringRepresentation(time: { hour: number; minute: number }): string {
    return `PT${time.hour}H${time.minute}M`;
  }
}
