"use client";
import { useEffect, useState } from "react";
import { productApi, categoryApi } from "@/lib/api";
import Link from "next/link";

export default function GraphQLDemo() {
  const [products, setProducts] = useState<any[]>([]);
  const [categories, setCategories] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [useGraphQL, setUseGraphQL] = useState(true);

  useEffect(() => {
    loadData();
  }, [useGraphQL]);

  const loadData = async () => {
    setLoading(true);
    try {
      if (useGraphQL) {
        const [productsRes, categoriesRes] = await Promise.all([
          productApi.getAllGraphQL(),
          categoryApi.getAllGraphQL(),
        ]);
        setProducts(productsRes.data.content);
        setCategories(categoriesRes.data.content);
      } else {
        const [productsRes, categoriesRes] = await Promise.all([
          productApi.getAll({ page: 0, size: 10 }),
          categoryApi.getAll(0, 10),
        ]);
        setProducts(productsRes.data.content);
        setCategories(categoriesRes.data.content);
      }
    } catch (error) {
      console.error("Error loading data:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="max-w-7xl mx-auto">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-bold">GraphQL vs REST Demo</h1>
          <div className="flex gap-4">
            <button
              onClick={() => setUseGraphQL(!useGraphQL)}
              className={`px-4 py-2 rounded ${useGraphQL ? "bg-blue-600 text-white" : "bg-gray-200"}`}
            >
              {useGraphQL ? "Using GraphQL" : "Using REST"}
            </button>
            <Link href="/" className="px-4 py-2 bg-gray-600 text-white rounded">Back to Home</Link>
          </div>
        </div>

        {loading ? (
          <div className="text-center py-12">Loading...</div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-xl font-bold mb-4">Categories ({categories.length})</h2>
              <div className="space-y-2">
                {categories.map((cat) => (
                  <div key={cat.id} className="border-b pb-2">
                    <p className="font-semibold">{cat.name}</p>
                    <p className="text-sm text-gray-600">{cat.description}</p>
                  </div>
                ))}
              </div>
            </div>

            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-xl font-bold mb-4">Products ({products.length})</h2>
              <div className="space-y-2">
                {products.map((product) => (
                  <div key={product.id} className="border-b pb-2">
                    <p className="font-semibold">{product.name}</p>
                    <p className="text-sm text-gray-600">${product.price} - {product.categoryName}</p>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        <div className="mt-8 bg-blue-50 border border-blue-200 rounded-lg p-6">
          <h3 className="font-bold text-lg mb-2">GraphQL Usage Locations:</h3>
          <ul className="list-disc list-inside space-y-1 text-sm">
            <li><code>frontend/lib/graphql.ts</code> - GraphQL client utility</li>
            <li><code>frontend/lib/api.ts</code> - getAllGraphQL() methods</li>
            <li><code>frontend/app/graphql-demo/page.tsx</code> - This demo page</li>
          </ul>
        </div>
      </div>
    </div>
  );
}
