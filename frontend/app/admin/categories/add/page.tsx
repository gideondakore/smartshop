'use client';
import { useState } from 'react';
import { categoryApi } from '@/lib/api';
import { useRouter } from 'next/navigation';

export default function AddCategory() {
  const router = useRouter();
  const [formData, setFormData] = useState({ name: '', description: '' });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await categoryApi.add(formData);
    router.push('/admin');
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-2xl mx-auto bg-white rounded-lg shadow p-8">
        <h1 className="text-3xl font-bold mb-6">Add Category</h1>
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label className="block mb-2">Name</label>
            <input type="text" value={formData.name} onChange={(e) => setFormData({ ...formData, name: e.target.value })} className="w-full border rounded px-3 py-2" required />
          </div>
          <div className="mb-6">
            <label className="block mb-2">Description</label>
            <textarea value={formData.description} onChange={(e) => setFormData({ ...formData, description: e.target.value })} className="w-full border rounded px-3 py-2" rows={3} />
          </div>
          <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 mr-2">Add Category</button>
          <button type="button" onClick={() => router.push('/admin')} className="bg-gray-600 text-white px-4 py-2 rounded hover:bg-gray-700">Cancel</button>
        </form>
      </div>
    </div>
  );
}
