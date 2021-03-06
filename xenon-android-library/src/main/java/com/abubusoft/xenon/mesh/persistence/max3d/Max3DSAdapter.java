package com.abubusoft.xenon.mesh.persistence.max3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import com.abubusoft.xenon.mesh.Mesh;
import com.abubusoft.xenon.mesh.MeshDrawModeType;
import com.abubusoft.xenon.mesh.MeshFactory;
import com.abubusoft.xenon.mesh.MeshHelper;
import com.abubusoft.xenon.mesh.MeshOptions;
import com.abubusoft.xenon.vbo.BufferManager;
import com.abubusoft.xenon.vbo.TextureBuffer;

public class Max3DSAdapter {

	public static class FlatVertex {
		float[] vertex = new float[3];
		float[] tex = new float[2];
		float[] normal = new float[3];

		HashSet<Integer> indexSet = new HashSet<>();

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((indexSet == null) ? 0 : indexSet.hashCode());
			result = prime * result + Arrays.hashCode(normal);
			result = prime * result + Arrays.hashCode(tex);
			result = prime * result + Arrays.hashCode(vertex);
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FlatVertex other = (FlatVertex) obj;
			if (!Arrays.equals(normal, other.normal))
				return false;
			if (!Arrays.equals(tex, other.tex))
				return false;
			if (!Arrays.equals(vertex, other.vertex))
				return false;
			return true;
		}

		private boolean isSameValue(float a, float b) {
			return Math.abs(a - b) < .00001;

		}
	}

	public static class FlatModel {
		public ArrayList<FlatVertex> list = new ArrayList<FlatVertex>();

		public boolean addFaces(int vertexIndex, FlatVertex currentVertex) {
			FlatVertex t;
			for (int i = 0; i < list.size(); i++) {
				t = list.get(i);

				if (t.equals(currentVertex)) {
					t.indexSet.add(vertexIndex);
					return false;
				}
			}

			// aggiungiamo al set di index anche questo indice
			currentVertex.indexSet.add(vertexIndex);
			list.add(currentVertex);

			return true;
		}

		public FlatVertex findVertex(int oldIndex) {
			FlatVertex t;
			for (int i = 0; i < list.size(); i++) {
				t = list.get(i);

				if (t.indexSet.contains(oldIndex)) {
					return t;
				}

			}

			return null;
		}

		/**
		 * dato il vecchio indice del vertice, lo converte nel nuovo indice.
		 * 
		 * @param oldIndex
		 * @return
		 */
		public int findTranslatedIndex(int oldIndex) {
			FlatVertex t;
			for (int i = 0; i < list.size(); i++) {
				t = list.get(i);

				if (t.indexSet.contains(oldIndex)) {
					return i;
				}

			}

			return -1;
		}
	}

	public static Mesh convertModelToIndexedTriangleShape(Max3dsModelData model, MeshOptions options) {
		Mesh mesh=MeshHelper.create(options);
				
		// prima di tutto ottimizziamo, ovvero trovare quei vertici che sono fondamentalmente uguali.
		// in questo formato ho notato che conviene definire un epsilon di confronto.
		FlatModel fm = new FlatModel();
		FlatVertex current;

		for (int i = 0; i < model.indices.length; i++) {
			current = new FlatVertex();
			current.vertex[0] = model.vertices[model.indices[i] * 3 + 0];
			current.vertex[1] = model.vertices[model.indices[i] * 3 + 1];
			current.vertex[2] = model.vertices[model.indices[i] * 3 + 2];

			if (options.textureEnabled) {
				current.tex[0] = model.textCoords[model.indices[i] * 2 + 0];
				current.tex[1] = model.textCoords[model.indices[i] * 2 + 1];
			}

			if (options.normalsEnabled) {
				current.normal[0] = model.normals[model.indices[i] * 3 + 0];
				current.normal[1] = model.normals[model.indices[i] * 3 + 1];
				current.normal[2] = model.normals[model.indices[i] * 3 + 2];
			}

			fm.addFaces(i, current);
		}

		options.indicesEnabled(true);

		// vertici min e max, servono per determinare il minimo ed il max
		float[] min = new float[3];
		float[] max = new float[3];

		// gli indici ci sono sempre
		{
			int nfaces = model.indices.length;
			mesh.indexesEnabled = true;
			mesh.indexesCount = nfaces*3;
			mesh.indexes = BufferManager.instance().createIndexBuffer(nfaces, options.bufferOptions.indexAllocation);

			for (int i = 0; i < nfaces; i += MeshFactory.VERTEX_PER_TRIANGLE) {
				mesh.indexes.values[i + 0] = (short) fm.findTranslatedIndex(model.indices[i + 0]);
				mesh.indexes.values[i + 1] = (short) fm.findTranslatedIndex(model.indices[i + 1]);
				mesh.indexes.values[i + 2] = (short) fm.findTranslatedIndex(model.indices[i + 2]);
			}

			// update
			mesh.indexes.update();
		}

		// vertici
		{
			int nvertex = fm.list.size();
			mesh.vertexCount = nvertex;
			mesh.vertices = BufferManager.instance().createVertexBuffer(nvertex, options.bufferOptions.vertexAllocation);

			for (int i = 0; i < nvertex * MeshFactory.VERTEX_DIMENSION; i += MeshFactory.VERTEX_DIMENSION) {
				current = fm.list.get(i / 3);
				mesh.vertices.coords[i + 0] = current.vertex[0];
				mesh.vertices.coords[i + 1] = current.vertex[1];
				mesh.vertices.coords[i + 2] = current.vertex[2];

				min[0] = Math.min(min[0], mesh.vertices.coords[i + 0]);
				min[1] = Math.min(min[1], mesh.vertices.coords[i + 1]);
				min[2] = Math.min(min[2], mesh.vertices.coords[i + 2]);

				max[0] = Math.max(max[0], mesh.vertices.coords[i + 0]);
				max[1] = Math.max(max[1], mesh.vertices.coords[i + 1]);
				max[2] = Math.max(max[2], mesh.vertices.coords[i + 2]);
			}

			// update
			mesh.vertices.update();
		}

		// impostiamo boundingbox
		mesh.boundingBox.set(Math.abs(max[0] - min[0]),Math.abs(max[1] - min[1]),Math.abs(max[2] - min[2]));

		// calcoliamo boundingSphere radius, ovvero il raggio della sfera che
		// contiene lo shape
		// Se parti da una sfera avente un raggio di 5 cm, il cubo inscritto è
		// quel cubo che può essere inserito esattamente nella tua sfera, in
		// modo tale che la distanza tra due vertici del cubo che siano opposti
		// tra loro misuri in lunghezza 10 cm (che è il diametro della sfera di
		// partenza). Per calcolare con precisione la lunghezza dello spigolo
		// del cubo inscritto devi dividere il diametro della sfera per la
		// radice quadrata di 3 (1,732 circa).
		// http://vivalascuola.studenti.it/come-determinare-le-misure-di-cubi-legati-a-sfere-140075.html#steps_2
		mesh.boundingSphereRadius = (float) (0.8660254037844386 * mesh.boundingBox.width);

		// texture
		if (options.textureEnabled) {
			int ntexture = fm.list.size();
			mesh.textures = new TextureBuffer[options.texturesCount];
			mesh.texturesEnabled = true;
			mesh.texturesCount = options.texturesCount;

			for (int t = 0; t < mesh.texturesCount; t++) {
				mesh.textures[t] = BufferManager.instance().createTextureBuffer(ntexture, options.bufferOptions.textureAllocation);

				for (int i = 0; i < ntexture * 2; i += 2) {
					current = fm.list.get(i / 2);
					mesh.textures[t].coords[i + 0] = current.tex[0];
					mesh.textures[t].coords[i + 1] = current.tex[1];
				}

				// update
				mesh.textures[t].update();
			}
		}

		// normal
		if (options.normalsEnabled) {
			int nnormals = fm.list.size();
			mesh.normalsEnabled = true;
			mesh.normals = BufferManager.instance().createVertexBuffer(nnormals, options.bufferOptions.textureAllocation);

			for (int i = 0; i < nnormals * 3; i += 3) {
				current = fm.list.get(i / 3);
				mesh.normals.coords[i + 0] = current.normal[0];
				mesh.normals.coords[i + 1] = current.normal[1];
				mesh.normals.coords[i + 2] = current.normal[2];
			}

			// update
			mesh.normals.update();
		}

		// stile di disegno
		mesh.drawMode = MeshDrawModeType.INDEXED_TRIANGLES;
		
		return mesh;

	}

}
