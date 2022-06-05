export class ErrorMessageParser {
  public static parseResponseToErrorMessage(response: any) {
    return response.error.split('"')[1];
  }
}
