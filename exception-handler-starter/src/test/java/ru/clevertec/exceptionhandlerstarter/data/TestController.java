package ru.clevertec.exceptionhandlerstarter.data;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.exceptionhandlerstarter.exception.AbstractException;
import ru.clevertec.exceptionhandlerstarter.exception.impl.BanException;
import ru.clevertec.exceptionhandlerstarter.exception.impl.NotFoundElementException;
import ru.clevertec.exceptionhandlerstarter.exception.impl.PatchException;
import ru.clevertec.exceptionhandlerstarter.exception.impl.CommunicationException;

@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping("/not-found-element")
    public void throwNotFoundElementException() {
        throw new NotFoundElementException("Not found element", 404);
    }

    @GetMapping("/patch")
    public void throwPatchException() {
        throw new PatchException("Patch", 400);
    }

    @GetMapping("/ban")
    public void throwBanException() {
        throw new BanException("Ban", 403);
    }

    @GetMapping("/communication")
    public void throwCommunication() {
        throw new CommunicationException("Communication", 403);
    }

    @GetMapping("/abstract")
    public void throwAbstract() {
        throw new AbstractException("Abstract", 500);
    }

}