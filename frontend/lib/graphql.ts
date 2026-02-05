const GRAPHQL_URL = process.env.NEXT_PUBLIC_API_URL?.replace('/api', '') + '/graphql' || 'http://localhost:8080/graphql';

export const graphqlRequest = async (query: string, variables?: any) => {
  const token = typeof window !== 'undefined' ? localStorage.getItem('token') : null;
  
  const response = await fetch(GRAPHQL_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...(token && { Authorization: `Bearer ${token}` }),
    },
    body: JSON.stringify({ query, variables }),
  });

  const result = await response.json();
  
  if (result.errors) {
    throw new Error(result.errors[0].message);
  }
  
  return result.data;
};
