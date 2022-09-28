package com.krazzzzymonkey.catalyst.utils.visual;


import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.managers.FileManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.codec.binary.Hex;
import org.codehaus.plexus.util.IOUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.lwjgl.opengl.GL20.*;

public class GLSLSandboxShader {

    InputStream inputStream = null;
    private final int programId;
    private final int timeUniform;
    private final int mouseUniform;
    private final int resolutionUniform;

    public GLSLSandboxShader(String fragmentShaderLocation) throws IOException {
        int program = glCreateProgram();


        File file = FileManager.getAssetFile("shader/passthrough.vsh");

        inputStream = new FileInputStream(file);
        glAttachShader(program, createShader(inputStream, GL_VERTEX_SHADER));
        inputStream = new FileInputStream(new File(fragmentShaderLocation));
        glAttachShader(program, createShader(inputStream, GL_FRAGMENT_SHADER));

        glLinkProgram(program);

        int linked = glGetProgrami(program, GL_LINK_STATUS);

        // If linking failed
        if (linked == 0) {
            System.err.println(glGetProgramInfoLog(program, glGetProgrami(program, GL_INFO_LOG_LENGTH)));

            throw new IllegalStateException("Shader failed to link");
        }

        this.programId = program;

        // Setup uniforms
        glUseProgram(program);

        this.timeUniform = glGetUniformLocation(program, "time");
        this.mouseUniform = glGetUniformLocation(program, "mouse");
        this.resolutionUniform = glGetUniformLocation(program, "resolution");

        glUseProgram(0);
    }

    public void useShader(int width, int height, float mouseX, float mouseY, float time) {
        glUseProgram(this.programId);

        glUniform2f(this.resolutionUniform, width, height);
        glUniform2f(this.mouseUniform, mouseX / width, 1.0f - mouseY / height);
        glUniform1f(this.timeUniform, time);
    }

    private int createShader(InputStream inputStream, int shaderType) throws IOException {
        int shader = glCreateShader(shaderType);

        glShaderSource(shader, readStreamToString(inputStream));

        glCompileShader(shader);

        int compiled = glGetShaderi(shader, GL_COMPILE_STATUS);

        // If compilation failed
        if (compiled == 0) {
            Main.logger.warn("Got error trying to compile shaderType: " + Integer.toHexString(shaderType));
            Main.logger.warn(glGetShaderInfoLog(shader, glGetShaderi(shader, GL_INFO_LOG_LENGTH)));
            throw new IllegalStateException("Failed to compile shader");
        }
        return shader;
    }

    private String readStreamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buffer = new byte[512];

        int read;

        while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
            out.write(buffer, 0, read);
        }
        //System.out.println(new String(out.toByteArray(), StandardCharsets.US_ASCII ));
        return new String(out.toByteArray(), StandardCharsets.US_ASCII);
    }

}
