package ntut.csie.ezScru.web.microservice.command;

import java.io.IOException;

public interface ICommand {
	Object Execute() throws IOException;
}
