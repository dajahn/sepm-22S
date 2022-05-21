package at.ac.tuwien.sepm.groupphase.backend.service;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public interface QRCodeGenerationService {

    /**
     * Generated a qr code representing the given input and returns it as a png in form of a byte[].
     *
     * @param input the payload of the qr code
     * @return png image as byte[] representing the qr code image
     */
    byte[] generate(String input);
}
