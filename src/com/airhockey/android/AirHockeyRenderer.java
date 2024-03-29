package com.airhockey.android;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.airhockey.android.util.LoggerConfig;
import com.airhockey.android.util.ShaderHelper;
import com.airhockey.android.util.TextResourceReader;

public class AirHockeyRenderer implements Renderer {
	private static final String U_COLOR = "u_Color";
	private static final String A_POSITION = "a_Position";
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int BYTES_PER_FLOAT = 4;
	private final FloatBuffer vertexData;
	private final Context context;
	private int program;
	private int uColorLocation;
	private int aPositionLocation;


	public AirHockeyRenderer(Context context)
	{
		this.context = context;
		float[] tableVerticesWithTriangles = {
				// Triangle 1
				-0.5f, -0.5f, 
				0.5f,  0.5f,
				-0.5f,  0.5f,

				// Triangle 2
				-0.5f, -0.5f, 
				0.5f, -0.5f, 
				0.5f,  0.5f,

				// Line 1
				-0.5f, 0f, 
				0.5f, 0f,

				// Mallets
				0f, -0.25f, 
				0f,  0.25f,
				// Puck
				0f, 0f
		};

		vertexData = ByteBuffer
				.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();

		vertexData.put(tableVerticesWithTriangles);
	}

	@Override
	public void onDrawFrame(GL10 glUnsued)
	{
		// Clear the rendering surface.
		glClear(GL_COLOR_BUFFER_BIT);

		// Change colour to white
		glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
		// Draw table as two triangles
		glDrawArrays(GL_TRIANGLES, 0, 6);

		// Change colour to red
		glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
		// Draw dividing line
		glDrawArrays(GL_LINES, 6, 2);

		// Draw the first mallet blue.
		glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
		glDrawArrays(GL_POINTS, 8, 1);
		// Draw the second mallet red.
		glUniform4f(uColorLocation, 1.0f, 0.0f, 1.0f, 1.0f);
		glDrawArrays(GL_POINTS, 9, 1);
		
		// Set the colour black
		glUniform4f(uColorLocation, 0f, 0f, 0f, 0f);
		glDrawArrays(GL_POINTS, 10, 1);
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height)
	{
		// Set the OpenGL viewport to fill the entire surface.
		glViewport(0, 0, width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
	{
		// Clear the Screen.
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Load the shader from file.
		String vertexShaderSource = TextResourceReader
				.readTextFileFromResource(context, R.raw.simple_vertex_shader);
		String fragmentShaderSource = TextResourceReader
				.readTextFileFromResource(context,R.raw.simple_fragment_shader);

		// Compile shaders
		int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
		int fragmentShader = ShaderHelper
				.compileFragmentShader(fragmentShaderSource);

		program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

		if (LoggerConfig.ON) {
			ShaderHelper.validateProgram(program);
		}

		glUseProgram(program);
		uColorLocation = glGetUniformLocation(program, U_COLOR);
		aPositionLocation = glGetAttribLocation(program, A_POSITION);

		vertexData.position(0);
		glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT,
				GL_FLOAT, false, 0, vertexData);
		// Enable the vertex array
		glEnableVertexAttribArray(aPositionLocation);
	}

}
